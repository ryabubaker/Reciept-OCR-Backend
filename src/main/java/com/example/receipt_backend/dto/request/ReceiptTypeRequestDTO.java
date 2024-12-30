package com.example.receipt_backend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Schema(description = "DTO for creating a new receipt type")
public class ReceiptTypeRequestDTO {

    @NotBlank(message = "Name is mandatory")
    @Schema(description = "Name of the receipt type", example = "Type A")
    private String name;

    private MultipartFile template;
}