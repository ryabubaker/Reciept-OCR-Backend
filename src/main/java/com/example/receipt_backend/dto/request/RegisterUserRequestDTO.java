package com.example.receipt_backend.dto.request;

import com.example.receipt_backend.utils.RoleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegisterUserRequestDTO {

    @NotBlank(message = "Username is required.")
    private String username;

    @NotBlank(message = "Email is required.")
    private String email;

    @NotBlank(message = "Password is required.")
    private String password;

    @NotBlank(message = "Tenant ID is required for self-registration.")
    private String tenantId;

    @NotNull(message = "Role type must not be null.")
    private RoleType roleType;
}
