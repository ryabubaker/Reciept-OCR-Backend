package com.example.receipt_backend.controller;

import com.example.receipt_backend.dto.ReceiptDTO;
import com.example.receipt_backend.dto.request.QueryDTO;
import com.example.receipt_backend.dto.request.UploadRequestDTO;
import com.example.receipt_backend.dto.response.GenericResponseDTO;
import com.example.receipt_backend.dto.response.UploadResponseDTO;
import com.example.receipt_backend.service.ReceiptService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/receipts")
@RequiredArgsConstructor
public class ReceiptController {

    private final ReceiptService receiptService;

    // Upload receipts
    @PostMapping("/upload")
    public ResponseEntity<GenericResponseDTO<String>> uploadReceipts(UploadRequestDTO requestDTO) {
           receiptService.uploadReceipts(requestDTO);
            return ResponseEntity.ok( new GenericResponseDTO<>("Receipts uploaded successfully", "200"));
    }

    @GetMapping("/list")
    public ResponseEntity<Page<ReceiptDTO>> listReceipts(@RequestBody QueryDTO requestDTO) {
        Page<ReceiptDTO> receipts = receiptService.listReceipts(requestDTO);
        return ResponseEntity.ok(receipts);
    }

    @GetMapping("/request/{requestId}")
    public ResponseEntity<UploadResponseDTO> getReceiptsByRequestId(
            @PathVariable UUID requestId) {
        UploadResponseDTO receipts = receiptService.getRequestById(requestId);
        return ResponseEntity.ok(receipts);
    }


    @PostMapping("/{receiptId}/confirm")
    public ResponseEntity<GenericResponseDTO<String>> approveReceipt(@PathVariable("receiptId") UUID receiptId) {
        receiptService.confirmReceipt(receiptId);
        return ResponseEntity.ok(new GenericResponseDTO<>("Receipt confirmed successfully", "200"));
    }


    @GetMapping("/{receiptId}")
    public ResponseEntity<ReceiptDTO> getReceiptByReceiptId(@PathVariable UUID receiptId) {
        ReceiptDTO receiptDTO = receiptService.getReceiptDetails(receiptId);
        return ResponseEntity.ok(receiptDTO);
    }

    @PatchMapping("/{receiptId}/ocr")
    public ResponseEntity<ReceiptDTO> updateOcrData(
            @PathVariable UUID receiptId,
            @RequestBody Map<String, String> updatedOcrData) {
        ReceiptDTO updated = receiptService.updateOcrData(receiptId, updatedOcrData);
        return ResponseEntity.ok(updated);
    }


    @DeleteMapping("/{receiptId}")
    public ResponseEntity<GenericResponseDTO<String>> deleteReceipt(@PathVariable UUID receiptId) {
            receiptService.deleteReceipt(receiptId);
        return ResponseEntity.ok(new GenericResponseDTO<>("Receipt deleted successfully", "200"));
    }

    @GetMapping("/pending")
    public ResponseEntity<Page<UploadResponseDTO>> getPendingReceipts(Pageable pageable) {
        Page<UploadResponseDTO> pendingReceipts = receiptService.getPendingRequests(pageable);
        return ResponseEntity.ok(pendingReceipts);
    }
}

