package com.example.receipt_backend.service.impl;

import com.example.receipt_backend.config.multitenant.CurrentTenantIdentifierResolverImpl;
import com.example.receipt_backend.dto.ReceiptDTO;
import com.example.receipt_backend.dto.UpdateReceiptDto;
import com.example.receipt_backend.dto.request.UploadRequestDTO;
import com.example.receipt_backend.dto.response.GenericResponseDTO;
import com.example.receipt_backend.dto.response.UploadResponseDTO;
import com.example.receipt_backend.entity.Receipt;
import com.example.receipt_backend.entity.ReceiptType;
import com.example.receipt_backend.entity.UploadRequest;
import com.example.receipt_backend.entity.User;
import com.example.receipt_backend.exception.BadRequestException;
import com.example.receipt_backend.exception.ErrorCode;
import com.example.receipt_backend.exception.ResourceNotFoundException;
import com.example.receipt_backend.mapper.ReceiptMapper;
import com.example.receipt_backend.mapper.UploadRequestMapper;
import com.example.receipt_backend.repository.*;
import com.example.receipt_backend.service.FileStorageService;
import com.example.receipt_backend.service.OcrService;
import com.example.receipt_backend.service.ReceiptService;
import com.example.receipt_backend.utils.ReceiptStatus;
import com.example.receipt_backend.utils.RequestStatus;
import com.example.receipt_backend.websocket.WebSocketNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;



@Service
@RequiredArgsConstructor
@Slf4j
public class ReceiptServiceImpl implements ReceiptService {

    private final ReceiptRepository receiptRepository;
    private final ReceiptMapper receiptMapper;
    private final UploadRequestMapper uploadRequestMapper;
    private final FileStorageService s3Service;
    private final OcrService ocrService;
    private final WebSocketNotificationService notificationService;
    private final UserRepository userRepository;
    private final UploadRequestRepository uploadRequestRepository;
    private final ReceiptTypeRepository receiptTypeRepository;


    @Transactional
    @Override
    public GenericResponseDTO<Boolean> uploadReceipts(UploadRequestDTO uploadRequestDto) {

        // Convert and save the UploadRequest entity
        UploadRequest uploadRequest = uploadRequestMapper.toEntity(uploadRequestDto);
        UploadRequest savedRequest = uploadRequestRepository.save(uploadRequest);

        UUID requestId = savedRequest.getRequestId();
        String tenantId = CurrentTenantIdentifierResolverImpl.getTenant();

        // Create and save Receipts
        List<Receipt> receipts = Arrays.stream(uploadRequestDto.getFiles())
                .map(file -> {
                    validateFile(file); // Validate the file
                    try {
                        // Upload file to S3 and get the URL
                        String s3Key = s3Service.uploadFile(file, tenantId, requestId.toString());
                        String imageUrl = s3Service.getFileUrl(s3Key);

                        // Fetch ReceiptType or throw exception if not found
                        ReceiptType receiptType = receiptTypeRepository
                                .findById(UUID.fromString(uploadRequestDto.getReceiptTypeId()))
                                .orElseThrow(() -> new ResourceNotFoundException(
                                        ErrorCode.RECEIPT_TYPE_NOT_FOUND
                                ));

                        // Create the Receipt entity
                        return receiptMapper.toEntity(imageUrl, savedRequest, receiptType);

                    } catch (IOException e) {
                        log.error("Failed to upload file: {}", file.getOriginalFilename(), e);
                        throw new BadRequestException(ErrorCode.FILE_UPLOAD_FAILED, e.getMessage());
                    }
                })
                .collect(Collectors.toList());

        savedRequest.getReceipts().addAll(receipts);
        receiptRepository.saveAll(receipts);

        // Initiate OCR processing asynchronously
        receipts.forEach(r -> ocrService.processReceiptAsync(r, savedRequest));

        return GenericResponseDTO.<Boolean>builder()
                .response(true)
                .build();
    }

    @Transactional
    @Override
    public ReceiptDTO approveReceipt(UUID receiptId, Map<String, Object> updatedValues) {

        // 1) Find the receipt
        Receipt receipt = receiptRepository.findById(receiptId).orElseThrow(() ->
                new ResourceNotFoundException(
                        ErrorCode.RECEIPT_NOT_FOUND
                ));

        // 2) Check that it's in a status that can be manually reviewed
        if (receipt.getStatus() != ReceiptStatus.PROCESSED &&
                receipt.getStatus() != ReceiptStatus.FAILED) {
            throw new BadRequestException(
                    ErrorCode.INVALID_INPUT,
                    "Receipt is not in a reviewable status"
            );
        }

        // 3) Update the values in receipt.ocrData
        //    (For now, we just store the current OCR data; you might expand logic here.)
        HashMap<Integer, String> currentOcrData = receipt.getOcrData();
        receipt.setOcrData(currentOcrData);

        // 4) Mark the receipt as APPROVED
        receipt.setStatus(ReceiptStatus.APPROVED);
        receipt.setApprovedBy(getCurrentUser());
        receipt.setApprovedAt(LocalDateTime.now());

        // 5) Save
        receipt = receiptRepository.save(receipt);

        // Check if all receipts in the request are now APPROVED
        boolean allConfirmed = receipt.getRequest()
                .getReceipts()
                .stream()
                .allMatch(r -> r.getStatus() == ReceiptStatus.APPROVED);

        if (allConfirmed) {
            receipt.getRequest().setStatus(RequestStatus.COMPLETED);
            uploadRequestRepository.save(receipt.getRequest());
        }

        // Convert to DTO
        return receiptMapper.toDTO(receipt);
    }

    @Transactional
    @Override
    public GenericResponseDTO<Boolean> deleteReceipt(UUID receiptId) {
        Receipt receipt = receiptRepository.findById(receiptId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.RECEIPT_NOT_FOUND
                ));

        UploadRequest request = receipt.getRequest();

        // Remove receipt from parent collection
        if (request != null) {
            request.getReceipts().remove(receipt);
            uploadRequestRepository.save(request); // Persist the updated parent
        }

        try {
            s3Service.deleteFile(receipt.getImageUrl());
            receiptRepository.delete(receipt);
            log.info("Receipt ID {} deleted successfully.", receiptId);
        } catch (Exception e) {
            log.error("Failed to delete receipt ID {}: {}", receiptId, e.getMessage(), e);
            throw new BadRequestException(
                    "Failed to delete receipt."
            );
        }

        // Delete parent request if no receipts remain
        if (request != null && request.getReceipts().isEmpty()) {
            uploadRequestRepository.delete(request);
        }

        return GenericResponseDTO.<Boolean>builder().response(true).build();
    }

    @Transactional
    @Override
    public void deleteReceipts(List<UUID> receiptIds) {
        for (UUID receiptId : receiptIds) {
            try {
                deleteReceipt(receiptId);
            } catch (ResourceNotFoundException e) {
                log.error("Receipt ID {} not found: {}", receiptId, e.getMessage());
                throw e;
            } catch (Exception e) {
                log.error("Failed to delete receipt ID {}: {}", receiptId, e.getMessage());
                throw new BadRequestException(
                        "Failed to delete receipt"
                );
            }
        }
    }

    @Transactional
    @Override
    public void updateReceiptsForApproval(List<UpdateReceiptDto> dtos) {
        for (UpdateReceiptDto updateReceiptDto : dtos) {
            // Find the receipt
            Receipt receipt = receiptRepository.findByReceiptId(UUID.fromString(updateReceiptDto.getReceiptId()))
                    .orElseThrow(() -> new ResourceNotFoundException(
                            ErrorCode.RECEIPT_NOT_FOUND
                    ));

            // Update the receipt's status and OCR data
            User user = getCurrentUser();
            receipt.setStatus(ReceiptStatus.valueOf(updateReceiptDto.getStatus()));
            receipt.setOcrData(updateReceiptDto.getOcrData());
            receipt.setApprovedBy(user);
            receipt.setApprovedAt(updateReceiptDto.getApprovedAt().toLocalDateTime());

            // Save the updated receipt
            receiptRepository.save(receipt);
        }
    }

    @Override
    @Transactional
    public List<UploadResponseDTO> getRequestsAfterDate(LocalDateTime dateTime) {
        return uploadRequestRepository.findAllByUploadedAtAfter(dateTime)
                .stream()
                .map(uploadRequestMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void updateRequestStatus(String requestId, String status) {
        UploadRequest request = uploadRequestRepository.findById(UUID.fromString(requestId))
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.REQUEST_NOT_FOUND
                ));

        request.setStatus(RequestStatus.valueOf(status));
        uploadRequestRepository.save(request);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<UploadResponseDTO> getPendingRequests(Pageable pageable) {
        return uploadRequestRepository.findByStatus(RequestStatus.PENDING, pageable)
                .map(uploadRequestMapper::toResponseDTO);
    }

    @Override
    public List<ReceiptDTO> listReceipts() {
        return receiptRepository.findAllByStatus(ReceiptStatus.APPROVED)
                .stream()
                .map(receiptMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public UploadResponseDTO getRequestById(UUID requestId) {
        UploadRequest request = uploadRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.REQUEST_NOT_FOUND
                ));

        return uploadRequestMapper.toResponseDTO(request);
    }

    @Override
    public ReceiptDTO getReceiptDetails(UUID receiptId) {
        Receipt receipt = receiptRepository.findById(receiptId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.RECEIPT_NOT_FOUND
                ));

        return receiptMapper.toDTO(receipt);
    }

    // Validate multipart file before upload
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BadRequestException(
                    ErrorCode.FILE_EMPTY.getMessage()
            );
        }

        String contentType = file.getContentType();
        if (contentType == null ||
                !List.of("image/jpeg", "image/png").contains(contentType.toLowerCase())) {
            throw new BadRequestException(
                    ErrorCode.INVALID_FILE_TYPE.getMessage()
            );
        }
    }
    public  User getCurrentUser() {
        if (SecurityContextHolder.getContext().getAuthentication() == null ||
                !SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
            throw new BadRequestException("User is not authenticated.");
        }

        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_RECORD_NOT_FOUND.getMessage()));
    }
}
