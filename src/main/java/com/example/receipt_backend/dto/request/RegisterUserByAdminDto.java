package com.example.receipt_backend.dto.request;

import com.example.receipt_backend.utils.RoleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterUserByAdminDto {
    @NotNull(message = "User ID must not be null.")
    private String userId;

    @NotBlank(message = "Username is required.")
    private String username;

    @NotBlank(message = "Email is required.")
    private String email;

    @NotNull(message = "Password must not be null.")
    private String password;

    @NotNull(message = "Role type must not be null.")
    private String roleType;

}
