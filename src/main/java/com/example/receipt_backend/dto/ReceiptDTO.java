package com.example.receipt_backend.dto;

import lombok.Data;

import java.util.Map;

@Data
public class ReceiptDTO {
    private Long receiptId;
    private String receiptImageUrl;
    private String uploadedAt;
    private String receiptFormatName;
    private Map<String, String> ocrData;
}

