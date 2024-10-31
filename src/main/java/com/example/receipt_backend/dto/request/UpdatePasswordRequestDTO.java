package com.example.receipt_backend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdatePasswordRequestDTO {

    private Long userId;

    @NotNull
    private String oldPassword;

    @NotNull
    private String newPassword;
}
