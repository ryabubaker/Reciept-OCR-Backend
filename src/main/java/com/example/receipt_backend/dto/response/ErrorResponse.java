package com.example.receipt_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
@Setter @Getter @AllArgsConstructor
public  class ErrorResponse {
    private HttpStatus status;
    private String message;
    private LocalDateTime timestamp;

}