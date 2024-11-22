package com.example.receipt_backend.dto.request;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class TenantRequestDTO {
    @NotBlank(message = "Tenant ID is required")
    private String tenantId;
    @NotBlank(message = "Company name is required")
    private String companyName;
    private String adminEmail;
}