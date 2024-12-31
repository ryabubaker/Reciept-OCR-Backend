package com.example.receipt_backend.exception;

import java.io.Serial;

// Bad Request Exception for invalid requests
public class BadRequestException extends RuntimeException {

    @Serial
    private final static  long serialVersionUID = 1L;

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
