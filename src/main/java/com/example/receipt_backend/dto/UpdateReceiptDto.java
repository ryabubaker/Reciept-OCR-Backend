package com.example.receipt_backend.dto;

import com.example.receipt_backend.utils.ReceiptStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.sql.Timestamp;
import java.util.HashMap;

@Data
public class UpdateReceiptDto {
    private String receiptId;
    private ReceiptStatus status;
    private HashMap<Integer, String> ocrData;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Timestamp approvedAt;
}