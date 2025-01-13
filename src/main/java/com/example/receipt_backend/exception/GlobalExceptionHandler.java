// src/main/java/com/example/receipt_backend/exception/GlobalExceptionHandler.java
package com.example.receipt_backend.exception;

import com.example.receipt_backend.dto.response.GenericResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.angus.mail.iap.ConnectionException;
import org.hibernate.PersistentObjectException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.servlet.http.HttpServletRequest;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    // Handle CustomAppException
    @ExceptionHandler(CustomAppException.class)
    public ResponseEntity<GenericResponseDTO<String>> handleCustomAppException(
            CustomAppException ex, HttpServletRequest request) {

        log.error("CustomAppException: {}", ex.getMessage());
        // Determine HTTP status based on ErrorCode
        HttpStatus status = mapErrorCodeToStatus(ex.getErrorCode());

        GenericResponseDTO<String> response = new GenericResponseDTO<>(ex.getErrorCode().getMessage());

        return new ResponseEntity<>(response, status);
    }

    // Handle ResourceNotFoundException (extends CustomAppException)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<GenericResponseDTO<String>> handleResourceNotFoundException(
            ResourceNotFoundException ex, HttpServletRequest request) {

        log.error("ResourceNotFoundException: {}", ex.getMessage());


        GenericResponseDTO<String> response = new GenericResponseDTO<>(ex.getErrorCode().getMessage());

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // Handle Method Argument Type Mismatch
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<GenericResponseDTO<String>> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {

        log.error("MethodArgumentTypeMismatchException: {}", ex.getMessage());

        GenericResponseDTO<String> response = new GenericResponseDTO<>("Invalid argument type: " + ex.getName());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Handle Persistent Object Exception (Database Errors)
    @ExceptionHandler(PersistentObjectException.class)
    public ResponseEntity<GenericResponseDTO<String>> handlePersistentObjectException(PersistentObjectException ex) {
        log.error("PersistentObjectException: {}", ex.getMessage(), ex);

        GenericResponseDTO<String> response = new GenericResponseDTO<>(ErrorCode.DATABASE_ERROR.getMessage());

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Handle Illegal Arguments
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<GenericResponseDTO<String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("IllegalArgumentException: {}", ex.getMessage());

        GenericResponseDTO<String> response = new GenericResponseDTO<>(
                ex.getMessage());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Handle Authentication Exceptions
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<GenericResponseDTO<String>> handleAuthenticationException(
            AuthenticationException ex, HttpServletRequest request) {

        log.error("AuthenticationException: {}", ex.getMessage());

        GenericResponseDTO<String> response = new GenericResponseDTO<>(
                ErrorCode.BAD_LOGIN_CREDENTIALS.getMessage());

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    // Handle Validation Exceptions (e.g., @Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GenericResponseDTO<String>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        log.error("MethodArgumentNotValidException: {}", ex.getMessage());

        // Extract validation error messages
        StringBuilder errorMessages = new StringBuilder();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errorMessages.append(error.getField()).append(": ").append(error.getDefaultMessage()).append("; ");
        });

        GenericResponseDTO<String> response = new GenericResponseDTO<>(
                errorMessages.toString());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Handle All Other Runtime Exceptions
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<GenericResponseDTO<String>> handleRuntimeException(
            RuntimeException ex, HttpServletRequest request) {

        log.error("RuntimeException: {}", ex.getMessage(), ex);

        GenericResponseDTO<String> response = new GenericResponseDTO<>(
                ErrorCode.INTERNAL_SERVER_ERROR.getMessage());

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ConnectionException.class)
    public ResponseEntity<GenericResponseDTO<String>> handleConnectionException(ConnectionException ex) {
        log.error("ConnectionException: {}", ex.getMessage());

        GenericResponseDTO<String> response = new GenericResponseDTO<>(
                "There is a problem connecting to the server. Please try again later.");

        return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
    }

    // Helper method to map ErrorCode to HTTP Status
    private HttpStatus mapErrorCodeToStatus(ErrorCode errorCode) {
        switch (errorCode) {

            // ---------------------------
            // 401 (Unauthorized)
            // ---------------------------
            case BAD_LOGIN_CREDENTIALS:
            case ACCOUNT_NOT_ACTIVATED:
            case UNAUTHORIZED_ACCESS:
                return HttpStatus.UNAUTHORIZED;

            // ---------------------------
            // 403 (Forbidden)
            // ---------------------------
            case ACCESS_DENIED:
            case INVALID_TENANT_ACCESS:
                return HttpStatus.FORBIDDEN;

            // ---------------------------
            // 404 (Not Found)
            // ---------------------------
            case USER_RECORD_NOT_FOUND:
            case TENANT_NOT_FOUND:
            case RECEIPT_NOT_FOUND:
            case REQUEST_NOT_FOUND:
            case RESOURCE_NOT_FOUND:
                return HttpStatus.NOT_FOUND;

            // ---------------------------
            // 409 (Conflict)
            // ---------------------------
            case USER_EMAIL_NOT_AVAILABLE:
            case USER_USERNAME_NOT_AVAILABLE:
            case USER_ALREADY_EXISTS:
            case TENANT_ALREADY_EXISTS:
            case RECEIPT_TYPE_AlREADY_EXISTS:
                return HttpStatus.CONFLICT;

            // ---------------------------
            // 413 (Payload Too Large)
            // ---------------------------
            case FILE_TOO_LARGE:
                return HttpStatus.PAYLOAD_TOO_LARGE;

            // ---------------------------
            // 500 (Internal Server Error)
            // ---------------------------
            case INTERNAL_SERVER_ERROR:
            case DATABASE_ERROR:
                return HttpStatus.INTERNAL_SERVER_ERROR;

            // ---------------------------
            // 400 (Bad Request)
            // ---------------------------
            case OLD_PASSWORD_DOESNT_MATCH:
            case MATCHING_VERIFICATION_RECORD_NOT_FOUND:
            case INVALID_PASSWORD_RESET_REQUEST:
            case PASSWORD_EMPTY:
            case RECEIPT_PROCESSING_FAILED:
            case OCR_EXTRACTION_FAILED:
            case FILE_EMPTY:
            case INVALID_FILE_TYPE:
            case FILE_UPLOAD_FAILED:
            case FILE_DOWNLOAD_FAILED:
            case NOTIFICATION_FAILED:
            case INVALID_INPUT:
            case MISSING_REQUIRED_FIELD:
            case INVALID_UUID_FORMAT:
            case INVALID_ARGUMENT_TYPE:
                return HttpStatus.BAD_REQUEST;

            // ---------------------------
            // Fallback
            // ---------------------------
            default:
                // If the error code doesn't match any known case,
                // default to 400 Bad Request or another status as appropriate.
                return HttpStatus.BAD_REQUEST;
        }
    }
}
