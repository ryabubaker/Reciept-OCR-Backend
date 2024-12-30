package com.example.receipt_backend.exception;

import com.example.receipt_backend.dto.response.GenericResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.PersistentObjectException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.servlet.http.HttpServletRequest;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<GenericResponseDTO<String>> handleResourceNotFoundException(
            ResourceNotFoundException ex, HttpServletRequest request) {

        log.error("ResourceNotFoundException: {}", ex.getMessage());
        GenericResponseDTO<String> response = new GenericResponseDTO<>(ex.getMessage(), null);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<GenericResponseDTO<String>> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {

        log.error("MethodArgumentTypeMismatchException: {}", ex.getMessage());
        GenericResponseDTO<String> response = new GenericResponseDTO<>("Invalid argument type", null);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(PersistentObjectException.class)
    public ResponseEntity<GenericResponseDTO> handlePersistentObjectException(PersistentObjectException ex) {
        GenericResponseDTO response = new GenericResponseDTO("Database Error", "A database error occurred.");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<GenericResponseDTO> handleIllegalArgumentException(IllegalArgumentException ex) {
        GenericResponseDTO response = new GenericResponseDTO("Invalid Input", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<GenericResponseDTO<String>> handleBadCredentialsException(
            AuthenticationException ex, HttpServletRequest request) {

        GenericResponseDTO<String> response = new GenericResponseDTO<>("Invalid username or password", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(CustomAppException.class)
    public ResponseEntity<GenericResponseDTO<String>> handleCustomAppException(
            CustomAppException ex, HttpServletRequest request) {

        log.error("CustomAppException: {}", ex.getMessage());
        GenericResponseDTO<String> response = new GenericResponseDTO<>(ex.getMessage(), ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }



    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<GenericResponseDTO<String>> handleRuntimeException(
            RuntimeException ex, HttpServletRequest request) {

        log.error("RuntimeException: {}", ex.getMessage(), ex);
        GenericResponseDTO<String> response = new GenericResponseDTO<>("Unexpected error occurred", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
