package com.example.receipt_backend.service;

import com.example.receipt_backend.dto.ReceiptDTO;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ReceiptService {
    @Transactional
    ReceiptDTO uploadReceipt(MultipartFile file, Long receiptFormatId);


    List<ReceiptDTO> getImageHistory(Long userId, Long tenantId, Pageable pageable);

    ReceiptDTO getImageDetails(Long receiptId);

    @Transactional
    ReceiptDTO updateImage(Long receiptId, MultipartFile file);

    @Transactional
    void deleteImage(Long receiptId);
}
