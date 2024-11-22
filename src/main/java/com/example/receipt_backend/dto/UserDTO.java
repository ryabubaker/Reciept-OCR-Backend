package com.example.receipt_backend.dto;


import com.example.receipt_backend.security.oauth.common.SecurityEnums;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Long id;
    private String tenantId;

    private String username;

    private String email;

    private boolean emailVerified;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private String imageUrl;

    private Set<String> roles;

    private String phoneNumber;

    private SecurityEnums.AuthProviderId registeredProviderName;

    private String registeredProviderId;

    private LocalDateTime createdDate;

    private LocalDateTime lastModifiedDate;
}
