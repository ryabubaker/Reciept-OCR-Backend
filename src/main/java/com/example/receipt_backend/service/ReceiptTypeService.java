// src/main/java/com/example/receipt_backend/service/ReceiptTypeService.java
package com.example.receipt_backend.service;


import com.example.receipt_backend.dto.request.ReceiptTypeRequestDTO;
import com.example.receipt_backend.dto.request.ReceiptTypeUpdateRequestDTO;
import com.example.receipt_backend.dto.response.GenericResponseDTO;
import com.example.receipt_backend.dto.response.ReceiptTypeResponseDTO;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ReceiptTypeService {

    @Transactional
    ReceiptTypeResponseDTO createReceiptType(ReceiptTypeRequestDTO dto) throws IOException;

    @Transactional(readOnly = true)
    ReceiptTypeResponseDTO getReceiptTypeById(String receiptTypeId);

    @Transactional(readOnly = true)
    List<Map<String, Object>> getAllReceiptTypes();

    @Transactional
    ReceiptTypeResponseDTO updateReceiptType(String receiptTypeId, ReceiptTypeRequestDTO requestDTO) throws IOException;

    @Transactional(readOnly = true)
    List<ReceiptTypeResponseDTO> getAllReceiptTypesWithJson();

    @Transactional
    GenericResponseDTO<Boolean> deleteReceiptType(String receiptTypeId) throws IOException;
}
