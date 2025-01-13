package com.example.receipt_backend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@Schema(description = "DTO for creating a new receipt type")
public class ReceiptTypeRequestDTO {
    @NotNull
    private String name;

    @NotNull
    private Map<String, Object> template;

    @NotNull
    private HashMap<String, Integer> column2idxMap;
}