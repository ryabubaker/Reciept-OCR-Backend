// src/main/java/com/example/receipt_backend/dto/UploadResponseDTO.java
package com.example.receipt_backend.dto.response;

import com.example.receipt_backend.dto.ReceiptDTO;
import com.example.receipt_backend.utils.RequestStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class UploadResponseDTO {
    private UUID requestId;
    private List<ReceiptDTO> receipts;
    private RequestStatus status;
    private String uploadedAt;
    private String uploadedBy;
}
