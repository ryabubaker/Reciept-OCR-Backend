package com.example.receipt_backend.dto.request;

import com.example.receipt_backend.dto.ReceiptFieldDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Schema(description = "DTO for creating a new receipt type")
public class ReceiptTypeRequestDTO {

    @NotBlank(message = "Name is mandatory")
    @Schema(description = "Name of the receipt type", example = "Type A")
    private String name;

    @Schema(description = "Description of the receipt type", example = "Restaurant Receipt")
    private String description;

    @NotNull(message = "Fields list cannot be null")
    @Schema(description = "List of fields associated with the receipt type")
    private List< @Valid ReceiptFieldDTO> fields;
}