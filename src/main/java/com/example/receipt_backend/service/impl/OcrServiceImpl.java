// src/main/java/com/example/receipt_backend/service/impl/OcrServiceImpl.java
package com.example.receipt_backend.service.impl;

import com.example.receipt_backend.entity.Receipt;
import com.example.receipt_backend.repository.ReceiptRepository;
import com.example.receipt_backend.service.OcrService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OcrServiceImpl implements OcrService {

    private final ReceiptRepository receiptRepository;
    private final ObjectMapper objectMapper;

    @Async("taskExecutor")
    @Override
    public void extractOcrAsync(Receipt receipt) {
        try {
            // Perform OCR
            Map<String, String> ocrDataMap = performOcr(receipt.getImageUrl());

            // Set ocrData
            receipt.setOcrData(ocrDataMap);

            // Save the updated receipt
            receiptRepository.save(receipt);
        } catch (Exception e) {
            log.error("Failed to process OCR for receipt: {}", receipt.getReceiptId(), e);
        }
    }

    private Map<String, String> performOcr(String imageUrl) {
        // Replace with actual OCR extraction logic
        // For demonstration, returning a sample map
        Map<String, String> ocrResult = new HashMap<>();
        ocrResult.put("recognizedText", "Sample OCR text");
        ocrResult.put("confidence", "0.98");
        return ocrResult;
    }
}
