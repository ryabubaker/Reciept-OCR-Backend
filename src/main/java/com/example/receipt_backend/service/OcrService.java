package com.example.receipt_backend.service;

import com.example.receipt_backend.dto.response.ReceiptProcessingResult;
import com.example.receipt_backend.entity.Receipt;
import com.example.receipt_backend.entity.UploadRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

public interface OcrService {
    @Async("taskExecutor")
    @Transactional
    void processReceiptAsync(Receipt receipt, UploadRequest request);
}
