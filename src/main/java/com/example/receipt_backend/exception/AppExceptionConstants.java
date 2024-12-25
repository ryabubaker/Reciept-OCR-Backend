package com.example.receipt_backend.exception;

public final class AppExceptionConstants {

    // -------------------
    // Authentication Exceptions
    // -------------------
    public static final String BAD_LOGIN_CREDENTIALS = "Bad credentials - Invalid username or password.";
    public static final String ACCOUNT_NOT_ACTIVATED = "Account not activated - Please verify your email or reprocess verification using forgot-password.";
    public static final String UNAUTHORIZED_ACCESS = "Insufficient authorization access.";

    // -------------------
    // User Exceptions
    // -------------------
    public static final String USER_RECORD_NOT_FOUND = "User doesn't exist.";
    public static final String USER_EMAIL_NOT_AVAILABLE = "This email isn't available.";
    public static final String USER_USERNAME_NOT_AVAILABLE = "This username isn't available.";
    public static final String USER_ALREADY_EXISTS = "User already exists.";
    public static final String OLD_PASSWORD_DOESNT_MATCH = "Old and new passwords do not match.";
    public static final String MATCHING_VERIFICATION_RECORD_NOT_FOUND = "Provided verification request doesn't seem correct.";
    public static final String INVALID_PASSWORD_RESET_REQUEST = "Provided password reset request doesn't seem correct.";

    // -------------------
    // Receipt Exceptions
    // -------------------
    public static final String RECEIPT_TYPE_NOT_FOUND = "Receipt format not found.";
    public static final String RECEIPT_NOT_FOUND = "Receipt not found.";
    public static final String RECEIPT_ALREADY_APPROVED = "Receipt has already been approved.";
    public static final String RECEIPT_ALREADY_REJECTED = "Receipt has already been rejected.";
    public static final String RECEIPT_TYPE_ALREADY_EXISTS = "Receipt format with this name already exists.";
    public static final String RECEIPT_TYPE_IN_USE_CANNOT_DELETE = "Receipt format is in use and cannot be deleted.";

    // -------------------
    // Tenant Exceptions
    // -------------------
    public static final String TENANT_NOT_FOUND = "Tenant not found.";
    public static final String INVALID_TENANT_OPERATION = "Invalid operation for the tenant.";
    public static final String INVALID_TENANT_ACCESS = "You do not have access to this tenant's resources.";

    // -------------------
    // File Exceptions
    // -------------------
    public static final String FILE_EMPTY = "File is empty.";
    public static final String INVALID_FILE_TYPE = "Invalid file type. Only JPEG and PNG are allowed.";
    public static final String FILE_TOO_LARGE = "File size exceeds the maximum limit of 5MB.";
    public static final String FILE_UPLOAD_FAILED = "File upload failed.";
    public static final String FILE_DOWNLOAD_FAILED = "File download failed.";

    // -------------------
    // OCR and Processing Exceptions
    // -------------------
    public static final String OCR_EXTRACTION_FAILED = "OCR data extraction failed.";
    public static final String RECEIPT_PROCESSING_FAILED = "Receipt processing failed.";

    // -------------------
    // Notification Exceptions
    // -------------------
    public static final String NOTIFICATION_FAILED = "Failed to send notification.";

    // -------------------
    // General Exceptions
    // -------------------
    public static final String INTERNAL_SERVER_ERROR = "An unexpected error occurred.";
    public static final String DATABASE_ERROR = "Database operation failed.";
    public static final String DUPLICATE_RESOURCE = "Duplicate resource exists.";
    public static final String INVALID_INPUT = "Invalid input provided.";
    public static final String MISSING_REQUIRED_FIELD = "Missing required field.";
    public static final String INVALID_UUID_FORMAT = "Invalid UUID format.";
    public static final String REQUEST_NOT_FOUND = "Upload request not found.";


    // -------------------
    // Access Exceptions
    // -------------------
    public static final String ACCESS_DENIED = "Access denied.";
    public static final String ROLE_NOT_FOUND = "Role not found.";
}
