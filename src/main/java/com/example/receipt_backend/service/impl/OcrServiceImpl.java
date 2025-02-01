package com.example.receipt_backend.service.impl;

import com.example.receipt_backend.dto.response.OcrResponse;
import com.example.receipt_backend.dto.response.OcrResult;
import com.example.receipt_backend.dto.response.ReceiptProcessingResult;
import com.example.receipt_backend.entity.Receipt;
import com.example.receipt_backend.entity.UploadRequest;
import com.example.receipt_backend.exception.BadRequestException;
import com.example.receipt_backend.ocr.OcrClient;
import com.example.receipt_backend.repository.ReceiptRepository;
import com.example.receipt_backend.repository.UploadRequestRepository;
import com.example.receipt_backend.service.FileStorageService;
import com.example.receipt_backend.service.OcrService;
import com.example.receipt_backend.utils.ReceiptStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class OcrServiceImpl implements OcrService {

    private final OcrClient ocrClient;
    private final ReceiptRepository receiptRepository;

    /**
     * Asynchronously processes a receipt by performing OCR and updating the receipt status.
     *
     * @param receipt The receipt entity to process.
     * @param request The upload request associated with the receipt.
     */
    @Async("taskExecutor")
    @Override
    @Transactional
    public void processReceiptAsync(Receipt receipt, UploadRequest request) {
        log.info("Starting OCR processing for receipt: {}", receipt.getReceiptId());

        try {
            // Perform OCR processing with retry mechanism
            OcrResponse ocrResponse = processWithRetry(receipt.getImageUrl(), receipt.getReceiptType().getTemplatePath());

            // Convert OCR response to processing result
            ReceiptProcessingResult result = convertOcrResponseToProcessingResult(ocrResponse);

            // Handle the OCR result
            handleOcrResult(receipt, request, result);
        } catch (Exception e) {
            // Handle any exceptions that occurred during OCR processing
            handleOcrError(receipt, e);
        }
    }

    /**
     * Converts the OCR response to a receipt processing result.
     *
     * @param ocrResponse The response from the OCR service.
     * @return The processing result containing extracted data.
     */
    private ReceiptProcessingResult convertOcrResponseToProcessingResult(OcrResponse ocrResponse) {
        if (ocrResponse == null || !"success".equalsIgnoreCase(ocrResponse.getStatus())) {
            throw new BadRequestException("OCR service returned unsuccessful status.");
        }

        // Convert OCR data keys from String to Integer
        HashMap<Integer, String> extractedData = ocrResponse.getData().stream()
                .map(this::convertOcrResultToMap)
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(
                        entry -> Integer.parseInt(entry.getKey().toString()),
                        Map.Entry::getValue,
                        (v1, v2) -> v1,
                        HashMap::new));

        return ReceiptProcessingResult.builder()
                .success(true)
                .extractedData(extractedData)
                .build();
    }


    /**
     * Handles the successful OCR processing by updating the receipt and saving it.
     *
     * @param receipt The receipt entity to update.
     * @param request The upload request associated with the receipt.
     * @param result  The result of the OCR processing.
     */
    private void handleOcrResult(Receipt receipt, UploadRequest request, ReceiptProcessingResult result) {
        if (result.isSuccess()) {
            receipt.setOcrData(result.getExtractedData());
            receipt.setStatus(ReceiptStatus.PROCESSED);
            receiptRepository.save(receipt);
            log.info("OCR processing succeeded for receipt: {}", receipt.getReceiptId());
        } else {
            receipt.setStatus(ReceiptStatus.FAILED);
            receiptRepository.save(receipt);
            log.error("OCR processing failed for receipt: {}", receipt.getReceiptId());
        }
    }

    /**
     * Handles any errors that occur during OCR processing by updating the receipt status.
     *
     * @param receipt The receipt entity to update.
     * @param e       The exception that occurred.
     */
    private void handleOcrError(Receipt receipt, Throwable e) {
        log.error("Exception during OCR processing for receipt {}: {}", receipt.getReceiptId(), e.getMessage(), e);
        receipt.setStatus(ReceiptStatus.FAILED);
        receiptRepository.save(receipt);
    }

    /**
     * Processes OCR with a retry mechanism. Retries up to 3 times with exponential backoff.
     *
     * @param fileUrl      The URL of the file to process.
     * @param templatePath The template path for OCR.
     * @return The OCR response.
     */
    @Retryable(
            retryFor = { Exception.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    public OcrResponse processWithRetry(String fileUrl, String templatePath) {
        log.info("Attempting OCR processing for file: {}", fileUrl);
        return ocrClient.processReceipt(fileUrl, templatePath);
    }

    /**
     * Converts an OCR result to a map of label-text pairs.
     *
     * @param ocrResult The OCR result to convert.
     * @return A map containing label-text pairs.
     */
    private Map<Integer, String> convertOcrResultToMap(OcrResult ocrResult) {
        Map<Integer, String> map = new HashMap<>();
        map.put(ocrResult.getId(), ocrResult.getText());
        return map;
    }
}
