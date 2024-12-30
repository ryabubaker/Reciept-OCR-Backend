package com.example.receipt_backend.dto.request;
import jakarta.validation.constraints.Email;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class TenantRequestDTO {
    @NotBlank(message = "Tenant name is required")
    private String tenantName;
    @NotBlank(message = "Admin email is required")
    @Email(message = "Invalid email format")
    private String adminEmail;
}