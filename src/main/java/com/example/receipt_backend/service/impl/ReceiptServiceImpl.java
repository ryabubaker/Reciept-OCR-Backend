// src/main/java/com/example/receipt_backend/service/impl/ReceiptServiceImpl.java
package com.example.receipt_backend.service.impl;

import com.example.receipt_backend.config.multitenant.CurrentTenantIdentifierResolverImpl;
import com.example.receipt_backend.dto.ReceiptDTO;

import com.example.receipt_backend.dto.request.UploadRequestDTO;
import com.example.receipt_backend.dto.response.UploadResponseDTO;
import com.example.receipt_backend.entity.Receipt;
import com.example.receipt_backend.entity.ReceiptType;
import com.example.receipt_backend.entity.UploadRequest;
import com.example.receipt_backend.exception.*;
import com.example.receipt_backend.mapper.ReceiptMapper;
import com.example.receipt_backend.mapper.UploadRequestMapper;
import com.example.receipt_backend.repository.*;
import com.example.receipt_backend.security.AppSecurityUtils;
import com.example.receipt_backend.service.FileStorageService;
import com.example.receipt_backend.service.OcrService;
import com.example.receipt_backend.service.ReceiptService;
import com.example.receipt_backend.utils.ReceiptStatus;
import com.example.receipt_backend.utils.RequestStatus;
import com.example.receipt_backend.websocket.WebSocketNotificationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
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
    private final UploadRequestRepository uploadRequestRepository;
    private final ReceiptTypeRepository receiptTypeRepository;

    @Transactional
    @Override
    public void uploadReceipts(UploadRequestDTO uploadRequestDto) {
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
                        // Create Receipt entity
                        ReceiptType receiptType = receiptTypeRepository.findById(UUID.fromString(uploadRequestDto.getReceiptTypeId())).orElseThrow(
                                () -> new ResourceNotFoundException(AppExceptionConstants.RECEIPT_TYPE_NOT_FOUND)
                        );
                        return receiptMapper.toEntity(imageUrl, savedRequest, receiptType);
                    } catch (IOException e) {
                        log.error("Failed to upload file: {}", file.getOriginalFilename(), e);
                        throw new BadRequestException(AppExceptionConstants.FILE_UPLOAD_FAILED, e);
                    }
                }).collect(Collectors.toList());

        savedRequest.getReceipts().addAll(receipts);
        receiptRepository.saveAll(receipts);

        // Initiate OCR processing asynchronously
        receipts.forEach(r -> ocrService.processReceiptAsync(r, savedRequest));
    }

    @Transactional
    @Override
    public ReceiptDTO approveReceipt(UUID receiptId, Map<String, Object> updatedValues) {
        // 1) Find the receipt
        Receipt receipt = receiptRepository.findById(receiptId).orElseThrow(() ->
                new ResourceNotFoundException(AppExceptionConstants.RECEIPT_NOT_FOUND));

        // 2) Check that it's in a status that can be manually reviewed
        if (receipt.getStatus() != ReceiptStatus.PROCESSED &&
                receipt.getStatus() != ReceiptStatus.FAILED) {
            throw new BadRequestException("Receipt is not in a reviewable status");
        }

        // 3) Update the values in receipt.ocrData
        List<Map<Integer, String>> currentOcrData = receipt.getOcrData();

        receipt.setOcrData(currentOcrData);

        // 4) Mark the receipt as APPROVED
        receipt.setStatus(ReceiptStatus.APPROVED);
        receipt.setApprovedBy(AppSecurityUtils.getCurrentUser());
        receipt.setApprovedAt(LocalDateTime.now());

        // 5) Save
        receipt = receiptRepository.save(receipt);

        // Check if all receipts in the request are confirmed
        boolean allConfirmed = receipt.getRequest().getReceipts().stream()
                .allMatch(r -> r.getStatus() == ReceiptStatus.APPROVED);
        if (allConfirmed) {
            receipt.getRequest().setStatus(RequestStatus.COMPLETED);
            uploadRequestRepository.save(receipt.getRequest());
        }

        // Convert to DTO or return the entity
        return receiptMapper.toDTO(receipt);
    }

    @Transactional
    @Override
    public void deleteReceipt(UUID receiptId) {
        Receipt receipt = receiptRepository.findById(receiptId)
                .orElseThrow(() -> new ResourceNotFoundException(AppExceptionConstants.RECEIPT_NOT_FOUND));

        UploadRequest request = receipt.getRequest();

        // Remove receipt from parent collection
        if (request != null) {
            request.getReceipts().remove(receipt);
            uploadRequestRepository.save(request); // Persist the change in the parent entity
        }

        try {
            s3Service.deleteFile(receipt.getImageUrl());
            receiptRepository.delete(receipt);
            log.info("Receipt ID {} deleted successfully.", receiptId);
        } catch (Exception e) {
            log.error("Failed to delete receipt ID {}: {}", receiptId, e.getMessage(), e);
            throw new BadRequestException("Failed to delete receipt.", e);
        }

        // Delete parent request if no receipts remain
        if (request != null && request.getReceipts().isEmpty()) {
            uploadRequestRepository.delete(request);
        }
    }


    @Override
    public Page<UploadResponseDTO> getPendingRequests(Pageable pageable) {
        return uploadRequestRepository.findByStatus(RequestStatus.PENDING, pageable).map(uploadRequestMapper::toResponseDTO);
    }



//    @Override
//    public Page<ReceiptDTO> listReceipts(QueryDTO queryDTO) {
//        UUID tenantId = getCurrentTenantId();
//
//        Specification<Receipt> spec = Specification.where(ReceiptSpecification.belongsToTenant(tenantId))
//                .and(ReceiptSpecification.containsSearchQuery(queryDTO.getSearchQuery()))
//                .and(ReceiptSpecification.hasStatus(queryDTO.getStatus()))
//                .and(ReceiptSpecification.uploadedAfter(queryDTO.getFromDate()))
//                .and(ReceiptSpecification.uploadedBefore(queryDTO.getToDate()));
//
//        Page<Receipt> receipts = receiptRepository.findAll(spec, queryDTO.getPageable());
//
//        log.info("Fetching receipts with filters - SearchQuery: {}, Status: {}, From: {}, To: {}",
//                queryDTO.getSearchQuery(),
//                queryDTO.getStatus(),
//                queryDTO.getFromDate(),
//                queryDTO.getToDate());
//
//        return receipts.map(receiptMapper::toDTO);
//    }

    @Transactional
    @Override
    public UploadResponseDTO getRequestById(UUID requestId) {
        UploadRequest request = uploadRequestRepository.findById(requestId).orElseThrow( ()
                -> new ResourceNotFoundException(AppExceptionConstants.REQUEST_NOT_FOUND));

        return uploadRequestMapper.toResponseDTO(request);
    }


    @Override
    public ReceiptDTO getReceiptDetails(UUID receiptId) {

        Receipt receipt = receiptRepository.findById(receiptId)
                .orElseThrow(() -> new ResourceNotFoundException("Receipt not found."));

        return receiptMapper.toDTO(receipt);
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BadRequestException(AppExceptionConstants.FILE_EMPTY);
        }

        String contentType = file.getContentType();
        if (contentType == null || !List.of("image/jpeg", "image/png").contains(contentType.toLowerCase())) {
            throw new BadRequestException(AppExceptionConstants.INVALID_FILE_TYPE);
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            throw new BadRequestException(AppExceptionConstants.FILE_TOO_LARGE);
        }
    }

}

