package com.example.receipt_backend.dto;

import com.example.receipt_backend.utils.ReceiptStatus;
import lombok.Data;

import java.util.HashMap;

@Data
public class UpdateReceiptDto {
    private String receiptId;
    private ReceiptStatus status;
    private HashMap<Integer, String> ocrData;
    private String approvedBy;
    private String approvedAt;
}