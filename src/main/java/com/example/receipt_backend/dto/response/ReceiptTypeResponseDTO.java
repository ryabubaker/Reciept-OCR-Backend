package com.example.receipt_backend.dto.response;

import com.example.receipt_backend.dto.ReceiptFieldDTO;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class ReceiptTypeResponseDTO {
    private UUID receiptTypeId;
    private String name;
    private String description;
    private List<ReceiptFieldDTO> fields;
}

