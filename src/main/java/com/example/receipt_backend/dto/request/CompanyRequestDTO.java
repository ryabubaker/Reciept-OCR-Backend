package com.example.receipt_backend.dto.request;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class CompanyRequestDTO {
    @NotBlank(message = "Company name is required")
    private String name;
}
