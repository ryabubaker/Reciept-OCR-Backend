package com.example.receipt_backend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Schema(description = "DTO for updating a receipt type")
public class ReceiptTypeUpdateRequestDTO {
    private String name;

    private MultipartFile template;
}