// src/main/java/com/example/receipt_backend/exception/ResourceNotFoundException.java
package com.example.receipt_backend.exception;

public class ResourceNotFoundException extends CustomAppException {
    public ResourceNotFoundException() {
        super(ErrorCode.RESOURCE_NOT_FOUND);
    }

    public ResourceNotFoundException(String message) {
        super(ErrorCode.RESOURCE_NOT_FOUND, message);
    }

    public ResourceNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}

