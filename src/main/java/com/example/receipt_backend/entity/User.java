package com.example.receipt_backend.entity;

import com.example.receipt_backend.entity.common.AbstractGenericPKAuditableEntity;
import com.example.receipt_backend.security.SecurityEnums;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
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
@Table(name = "users", schema = "public")
public class User extends AbstractGenericPKAuditableEntity implements Serializable {
    @Serial
    private final static  long serialVersionUID = 1L;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @JsonProperty(value = "password", access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    // TODO @Email Validation
    @Column(nullable = false)
    private String email;

    @Column(name = "email_verified")
    private boolean emailVerified;

    @Column(name = "image_url")
    private String imageUrl;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinTable(
            name = "user_roles",
            schema = "public", // Specify the schema explicitly
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<RoleEntity> roles;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = true)
    private Tenant tenant;

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
