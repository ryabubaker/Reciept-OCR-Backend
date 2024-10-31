package com.example.receipt_backend.dto.request;

import lombok.Data;

@Data
public class RegisterUserRequestDTO {

    private String username;

    private String email;

    private String password;
}
