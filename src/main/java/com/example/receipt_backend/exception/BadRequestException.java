package com.example.receipt_backend.exception;

import java.io.Serial;

// Bad Request Exception for invalid requests
public class BadRequestException extends CustomAppException {


    public BadRequestException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public BadRequestException(String message) {
        super(ErrorCode.BAD_REQUEST, message);
    }


}
