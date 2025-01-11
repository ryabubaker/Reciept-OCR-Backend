package com.example.receipt_backend.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    private String invitationToken;
    private String username;
    private String email;
    private String password;
    private String confirmPassword;
}