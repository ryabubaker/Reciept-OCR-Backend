package com.example.receipt_backend.utils;

public enum ReceiptStatus {
    PENDING,    // Just uploaded, OCR not yet done
    PROCESSED,  // OCR succeeded
    FAILED,     // OCR failed
    APPROVED    // Manually reviewed + approved
}
