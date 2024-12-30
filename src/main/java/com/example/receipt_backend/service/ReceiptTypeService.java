// src/main/java/com/example/receipt_backend/service/ReceiptTypeService.java
package com.example.receipt_backend.service;


import com.example.receipt_backend.dto.request.ReceiptTypeRequestDTO;
import com.example.receipt_backend.dto.request.ReceiptTypeUpdateRequestDTO;
import com.example.receipt_backend.dto.response.ReceiptTypeResponseDTO;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

public interface ReceiptTypeService {

    @Transactional
    ReceiptTypeResponseDTO createReceiptType(ReceiptTypeRequestDTO dto) throws IOException;

    @Transactional(readOnly = true)
    ReceiptTypeResponseDTO getReceiptTypeByName(String receiptTypeName);

    @Transactional(readOnly = true)
    List<String> getAllReceiptTypes();

    @Transactional
    ReceiptTypeResponseDTO updateReceiptType(String receiptTypeName, ReceiptTypeUpdateRequestDTO requestDTO) throws IOException;

    @Transactional
    void deleteReceiptType(String receiptTypeName) throws IOException;
}
