package com.example.receipt_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tenants", schema = "public")
@Getter
@Setter
public class Tenant {
    @Id
    private String tenantId;

    @Column(nullable = false)
    private String companyName;

    @Column(nullable = false)
    private String adminEmail;


    public Tenant(String companyName, String adminEmail) {
        this.tenantId = UUID.randomUUID().toString();  // Generate a unique tenant ID
        this.companyName = companyName;
        this.adminEmail = adminEmail;
    }

    public Tenant() {

    }
}

