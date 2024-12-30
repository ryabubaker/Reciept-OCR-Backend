package com.example.receipt_backend.service;

import com.example.receipt_backend.dto.ReceiptDTO;
import com.example.receipt_backend.dto.request.QueryDTO;
import com.example.receipt_backend.dto.request.UploadRequestDTO;
import com.example.receipt_backend.dto.response.UploadResponseDTO;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;
import java.util.UUID;

public interface ReceiptService {


    @Transactional
    void uploadReceipts(UploadRequestDTO requestDTO);

    @Transactional
    UploadResponseDTO getRequestById(UUID requestId);

    ReceiptDTO getReceiptDetails(UUID receiptId);

//    @Transactional
//    ReceiptDTO updateOcrData(UUID receiptId, Map<String, String> updatedOcrData);

    Page<UploadResponseDTO> getPendingRequests(Pageable pageable);

    @Transactional
    ReceiptDTO approveReceipt(UUID receiptId, Map<String, Object> updatedValues);

    @Transactional
    void deleteReceipt(UUID receiptId);

}
