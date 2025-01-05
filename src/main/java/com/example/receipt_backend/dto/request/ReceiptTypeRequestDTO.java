package com.example.receipt_backend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

@Data
@Schema(description = "DTO for creating a new receipt type")
public class ReceiptTypeRequestDTO {

    private String name;

    private Map<String, Object> template;
}