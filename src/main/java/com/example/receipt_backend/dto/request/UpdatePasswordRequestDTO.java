package com.example.receipt_backend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class UpdatePasswordRequestDTO {

    private UUID userId;

    @NotNull
    private String oldPassword;

    @NotNull
    private String newPassword;
}
