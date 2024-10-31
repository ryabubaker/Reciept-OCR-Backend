package com.example.receipt_backend.entity;

import com.example.receipt_backend.entity.common.AbstractGenericPKAuditableEntity;
import com.example.receipt_backend.security.oauth.common.SecurityEnums;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class UserEntity extends AbstractGenericPKAuditableEntity {


    @Column( name = "username", nullable = false, unique = true)
    private String username;

    @JsonProperty( value = "password", access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    // TODO @Email Validation
    @Column(nullable = false)
    private String email;

    @Column(name = "email_verified")
    private boolean emailVerified;

    @Column(name = "image_url")
    private String imageUrl;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<RoleEntity> roles = new HashSet<>();


    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "registered_provider_name")
    @Enumerated(EnumType.STRING)
    private SecurityEnums.AuthProviderId registeredProviderName;

    @Column(name = "registered_provider_id")
    private String registeredProviderId;

    @Column(name = "verification_code")
    private String verificationCode;

    @Column(name = "verification_code_expires_at")
    private Instant verificationCodeExpiresAt;

    // Constructors, getters, and setters


}
