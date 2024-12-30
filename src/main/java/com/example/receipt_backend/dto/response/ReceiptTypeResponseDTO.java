package com.example.receipt_backend.dto.response;

import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class ReceiptTypeResponseDTO {
    private String name;

    private Map<String, Object> template;
}

