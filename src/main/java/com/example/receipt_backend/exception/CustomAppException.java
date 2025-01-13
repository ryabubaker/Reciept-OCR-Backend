// src/main/java/com/example/receipt_backend/exception/CustomAppException.java
package com.example.receipt_backend.exception;

import lombok.Getter;

import java.sql.SQLException;

@Getter
public class CustomAppException extends RuntimeException {
    private final ErrorCode errorCode;

    public CustomAppException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public CustomAppException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }


    public CustomAppException(String s) {
        super(s);
        this.errorCode = ErrorCode.DATABASE_ERROR;
    }
}
