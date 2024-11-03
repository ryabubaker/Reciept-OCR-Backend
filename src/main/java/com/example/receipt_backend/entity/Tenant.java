package com.example.receipt_backend.entity;

import com.example.receipt_backend.entity.common.AbstractGenericPKAuditableEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
public class Tenant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name; // Company name

    private String contactInfo; // Contact information if needed

    @OneToMany(mappedBy = "tenant")
    private List<User> users; // List of users belonging to the tenant

    @OneToMany(mappedBy = "tenant")
    private List<ReceiptTemplate> receiptTemplates; // Templates for receipt formats




}
