package com.example.receipt_backend.exception;

import java.io.Serial;

// Base exception for custom application errors
public class CustomAppException extends RuntimeException {

    @Serial
    private final static  long serialVersionUID = 1L;

    public CustomAppException() {
        super();
    }

    public CustomAppException(String message) {
        super(message);
    }

    public CustomAppException(String message, Throwable cause) {
        super(message, cause);
    }

    public CustomAppException(Throwable cause) {
        super(cause);
    }
}
