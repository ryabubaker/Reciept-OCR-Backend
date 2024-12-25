package com.example.receipt_backend.dto.request;// src/main/java/com/example/receipt_backend/dto/ReceiptUploadRequestDTO.java

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;


import java.util.UUID;

@Data
public class ReceiptUploadRequestDTO {
    @NotEmpty(message = "Files must not be empty")
    private MultipartFile[] files;

    @NotNull(message = "Receipt Type ID must not be null")
    private UUID receiptTypeId;
}
