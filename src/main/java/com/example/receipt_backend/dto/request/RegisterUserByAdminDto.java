package com.example.receipt_backend.dto.request;

import com.example.receipt_backend.utils.RoleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterUserByAdminDto {

    @NotBlank(message = "Email is required.")
    private String email;

    @NotNull(message = "Role type must not be null.")
    private String roleType;

}
