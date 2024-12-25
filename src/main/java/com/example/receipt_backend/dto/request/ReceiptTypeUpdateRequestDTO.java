package com.example.receipt_backend.dto.request;

import com.example.receipt_backend.dto.ReceiptFieldDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "DTO for updating a receipt type")
public class ReceiptTypeUpdateRequestDTO {

    @Schema(description = "Description of the receipt type", example = "Grocery receipt")
    private String description;

    @Schema(description = "List of fields associated with the receipt type")
    private List<@Valid ReceiptFieldDTO> fields;
}