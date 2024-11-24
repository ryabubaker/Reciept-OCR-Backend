package com.example.receipt_backend.controller;

import com.example.receipt_backend.dto.ReceiptDTO;
import com.example.receipt_backend.service.ReceiptService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/receipts")
@RequiredArgsConstructor
public class ReceiptController {

    private final ReceiptService receiptService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadReceipt(@RequestParam("file") MultipartFile file,
                                           @RequestParam("receiptFormatId") Long receiptFormatId) {
        ReceiptDTO receiptDTO = receiptService.uploadReceipt(file, receiptFormatId);
        return ResponseEntity.ok(receiptDTO);
    }

    @GetMapping("/history")
    public ResponseEntity<?> getImageHistory(@RequestParam("userId") Long userId,
                                             @RequestParam(value = "tenantId", required = false) Long tenantId,
                                             @PageableDefault(size = 10) Pageable pageable) {
        List<ReceiptDTO> history = receiptService.getImageHistory(userId, tenantId, pageable);
        return ResponseEntity.ok(history);
    }


    @GetMapping("/{receiptId}")
    public ResponseEntity<?> getImageDetails(@PathVariable("receiptId") Long receiptId) {
        ReceiptDTO receiptDTO = receiptService.getImageDetails(receiptId);
        return ResponseEntity.ok(receiptDTO);
    }

    @PutMapping("/update/{receiptId}")
    public ResponseEntity<?> updateImage(@PathVariable("receiptId") Long receiptId,
                                         @RequestParam("file") MultipartFile file) {
        ReceiptDTO updatedReceipt = receiptService.updateImage(receiptId, file);
        return ResponseEntity.ok(updatedReceipt);
    }

    @DeleteMapping("/delete/{receiptId}")
    public ResponseEntity<?> deleteImage(@PathVariable("receiptId") Long receiptId) {
        receiptService.deleteImage(receiptId);
        return ResponseEntity.ok("Receipt deleted successfully.");
    }
}
