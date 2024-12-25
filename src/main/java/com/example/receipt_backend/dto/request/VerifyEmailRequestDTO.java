package com.example.receipt_backend.dto.request;

import com.example.receipt_backend.security.SecurityEnums;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class VerifyEmailRequestDTO {

    private String email;

    private String verificationCode;

    @JsonProperty("registeredProviderName")
    private SecurityEnums.AuthProviderId authProviderId;
}
