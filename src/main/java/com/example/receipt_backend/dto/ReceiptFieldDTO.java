package com.example.receipt_backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReceiptFieldDTO {

    @NotBlank(message = "Field name cannot be blank")
    private String fieldName;

    @NotBlank(message = "Field type cannot be blank")
    private String fieldType; // e.g., "string", "number", "date"
}