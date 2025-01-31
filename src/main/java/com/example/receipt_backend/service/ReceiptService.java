package com.example.receipt_backend.service;

import com.example.receipt_backend.dto.ReceiptDTO;
import com.example.receipt_backend.dto.UpdateReceiptDto;
import com.example.receipt_backend.dto.request.UploadRequestDTO;
import com.example.receipt_backend.dto.response.GenericResponseDTO;
import com.example.receipt_backend.dto.response.UploadResponseDTO;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ReceiptService {


    @Transactional
    GenericResponseDTO<Boolean> uploadReceipts(UploadRequestDTO requestDTO);

    List<ReceiptDTO> listReceipts();

    @Transactional
    UploadResponseDTO getRequestById(UUID requestId);

    ReceiptDTO getReceiptDetails(UUID receiptId);

//    @Transactional
//    ReceiptDTO updateOcrData(UUID receiptId, Map<String, String> updatedOcrData);

    void updateRequestStatus(String requestId, String status);

    Page<UploadResponseDTO> getPendingRequests(Pageable pageable);

    @Transactional
    ReceiptDTO approveReceipt(UUID receiptId, Map<String, Object> updatedValues);

    @Transactional
    GenericResponseDTO<Boolean> deleteReceipt(UUID receiptId);

    void deleteReceipts(List<UUID> receiptIds);


    @Transactional
    void updateReceiptsForApproval(List<UpdateReceiptDto> dtos);

    List<UploadResponseDTO> getRequestsAfterDate(LocalDateTime parsedDateTime);
}
