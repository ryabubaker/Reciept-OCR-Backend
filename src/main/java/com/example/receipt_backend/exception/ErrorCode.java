package com.example.receipt_backend.exception;

public enum ErrorCode {

    // Authentication Exceptions
    BAD_LOGIN_CREDENTIALS("Bad credentials - Invalid username or password."),
    ACCOUNT_NOT_ACTIVATED("Account not activated. Please verify your email or reprocess verification via forgot-password."),
    UNAUTHORIZED_ACCESS("Insufficient authorization access."),
    ACCESS_DENIED("Access denied."),

    // User Exceptions
    USER_RECORD_NOT_FOUND("User doesn't exist."),
    USER_EMAIL_NOT_AVAILABLE("This email isn't available."),
    USER_USERNAME_NOT_AVAILABLE("This username isn't available."),
    USER_ALREADY_EXISTS("User already exists."),
    OLD_PASSWORD_DOESNT_MATCH("Old and new passwords do not match."),
    MATCHING_VERIFICATION_RECORD_NOT_FOUND("Provided verification request doesn't seem correct."),
    INVALID_PASSWORD_RESET_REQUEST("Provided password reset request doesn't seem correct."),
    PASSWORD_EMPTY("Password cannot be empty."),

    // Tenant Exceptions
    TENANT_NOT_FOUND("Tenant not found."),
    TENANT_ALREADY_EXISTS("Tenant with this name already exists."),
    INVALID_TENANT_ACCESS("You do not have access to this tenant's resources."),

    // Receipt Type Exceptions
    RECEIPT_TYPE_NOT_FOUND("Receipt type not found."),
    RECEIPT_TYPE_AlREADY_EXISTS("Receipt type with this name already exists."),

    // Receipt Exceptions
    RECEIPT_NOT_FOUND("Receipt not found."),
    RECEIPT_PROCESSING_FAILED("Receipt processing failed."),
    OCR_EXTRACTION_FAILED("OCR data extraction failed."),

    // File Exceptions
    FILE_EMPTY("File is empty."),
    INVALID_FILE_TYPE("Invalid file type. Only JPEG and PNG are allowed."),
    FILE_TOO_LARGE("File size exceeds the maximum limit."),
    FILE_UPLOAD_FAILED("File upload failed."),
    FILE_DOWNLOAD_FAILED("File download failed."),

    // Notification Exceptions
    NOTIFICATION_FAILED("Failed to send notification."),

    // General Exceptions
    INTERNAL_SERVER_ERROR("An unexpected error occurred."),
    INVALID_INPUT("Invalid input provided."),
    MISSING_REQUIRED_FIELD("Missing required field."),
    INVALID_UUID_FORMAT("Invalid UUID format."),
    REQUEST_NOT_FOUND("Request not found."),
    RESOURCE_NOT_FOUND("Resource not found."),
    INVALID_ARGUMENT_TYPE("Invalid argument type."),
    DATABASE_ERROR("Database operation failed."),
    BAD_REQUEST("Bad request. Incorrect request format or missing required parameters.");

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
