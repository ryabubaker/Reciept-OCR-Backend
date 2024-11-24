package com.example.receipt_backend.service.impl;

import com.example.receipt_backend.config.multitenant.CurrentTenantIdentifierResolverImpl;
import com.example.receipt_backend.dto.ReceiptDTO;
import com.example.receipt_backend.entity.Receipt;
import com.example.receipt_backend.entity.ReceiptType;
import com.example.receipt_backend.entity.User;
import com.example.receipt_backend.exception.BadRequestException;
import com.example.receipt_backend.exception.ResourceNotFoundException;
import com.example.receipt_backend.mapper.ReceiptMapper;
import com.example.receipt_backend.repository.ReceiptRepository;
import com.example.receipt_backend.repository.ReceiptTypeRepository;
import com.example.receipt_backend.repository.UserRepository;
import com.example.receipt_backend.service.FileStorageService;
import com.example.receipt_backend.service.ReceiptService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReceiptServiceImpl implements ReceiptService {

    private final ReceiptRepository receiptRepository;
    private final ReceiptTypeRepository receiptTypeRepository;
    private final UserRepository userRepository;
    private final ReceiptMapper receiptMapper;

    private final FileStorageService fileStorageService;

    @Transactional
    @Override
    public ReceiptDTO uploadReceipt(MultipartFile file, Long receiptFormatId) {
        ReceiptType receiptType = receiptTypeRepository.findById(receiptFormatId)
                .orElseThrow(() -> new ResourceNotFoundException("Receipt format not found"));

        String currentTenantId = CurrentTenantIdentifierResolverImpl.getTenant();
        if (!receiptType.getTenant().getTenantId().equals(currentTenantId)) {
            throw new BadRequestException("Receipt format does not belong to the current tenant.");
        }

        String fileUrl = fileStorageService.storeFile(file);

        Receipt receipt = new Receipt();
        receipt.setReceiptType(receiptType);
        receipt.setReceiptImageUrl(fileUrl);
        receipt.setUploadedAt(LocalDateTime.now().toString());
        receipt.setTenant(receiptType.getTenant());
        receipt.setUser(getCurrentUser());

        return receiptMapper.toDTO(receiptRepository.save(receipt));
    }


    @Override
    public List<ReceiptDTO> getImageHistory(Long userId, Long tenantId, Pageable pageable) {
        Page<Receipt> receipts = (tenantId != null)
                ? receiptRepository.findByTenantIdAndUserId(tenantId, userId, pageable)
                : receiptRepository.findByUserId(userId, pageable);
        return receipts.map(receiptMapper::toDTO).getContent();
    }
    @Override
    public ReceiptDTO getImageDetails(Long receiptId) {
        Receipt receipt = receiptRepository.findById(receiptId)
                .orElseThrow(() -> new ResourceNotFoundException("Receipt not found"));
        validateUserAccess(receipt.getUser().getId());
        return receiptMapper.toDTO(receipt);
    }

    @Transactional
    @Override
    public ReceiptDTO updateImage(Long receiptId, MultipartFile file) {
        Receipt receipt = receiptRepository.findById(receiptId)
                .orElseThrow(() -> new ResourceNotFoundException("Receipt not found"));

        validateUserAccess(receipt.getUser().getId());

        String fileUrl = fileStorageService.storeFile(file);
        receipt.setReceiptImageUrl(fileUrl);

        return receiptMapper.toDTO(receiptRepository.save(receipt));
    }

    @Transactional
    @Override
    public void deleteImage(Long receiptId) {
        Receipt receipt = receiptRepository.findById(receiptId)
                .orElseThrow(() -> new ResourceNotFoundException("Receipt not found"));

        validateUserAccess(receipt.getUser().getId());
        fileStorageService.deleteFile(receipt.getReceiptImageUrl());
        receiptRepository.delete(receipt);
    }

    private User getCurrentUser() {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private void validateUserAccess(Long ownerId) {
        User currentUser = getCurrentUser();
        if (!currentUser.getId().equals(ownerId)) {
            throw new BadRequestException("You do not have access to this receipt.");
        }
    }

}
