// src/main/java/com/example/receipt_backend/service/ReceiptTypeService.java
package com.example.receipt_backend.service;


import com.example.receipt_backend.dto.request.ReceiptTypeRequestDTO;
import com.example.receipt_backend.dto.request.ReceiptTypeUpdateRequestDTO;
import com.example.receipt_backend.dto.response.ReceiptTypeResponseDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface ReceiptTypeService {

    @Transactional
    ReceiptTypeResponseDTO createReceiptType(ReceiptTypeRequestDTO requestDTO);

    @Transactional(readOnly = true)
    ReceiptTypeResponseDTO getReceiptTypeById(UUID receiptTypeId);

    @Transactional(readOnly = true)
    List<ReceiptTypeResponseDTO> getAllReceiptTypes();

    @Transactional
    ReceiptTypeResponseDTO updateReceiptType(UUID receiptTypeId, ReceiptTypeUpdateRequestDTO requestDTO);

    @Transactional
    void deleteReceiptType(UUID receiptTypeId);
    @Transactional
    void deleteReceiptField(UUID receiptTypeId, String fieldName);
}
