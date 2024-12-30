package com.example.receipt_backend.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class ReceiptProcessingResult {
    private boolean success;
    private List<Map<String, String>> extractedData;
    private String error;
}