// src/main/java/com/example/receipt_backend/dto/ReceiptDTO.java
package com.example.receipt_backend.dto;

import com.example.receipt_backend.utils.ReceiptStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class ReceiptDTO {
    private String receiptId;
    private String receiptTypeId;
    private String receiptTypeName;
    private String imageUrl;
    private ReceiptStatus status;
    private List<Map<Integer, String>> ocrData;
    private String approvedBy;
    private String approvedAt;
}
