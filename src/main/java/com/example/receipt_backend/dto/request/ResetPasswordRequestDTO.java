package com.example.receipt_backend.dto.request;

import lombok.Data;

@Data
public class ResetPasswordRequestDTO {

    private String email;

    private String forgotPasswordVerCode;

    private String newPassword;
}
