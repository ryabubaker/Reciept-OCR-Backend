package com.example.receipt_backend.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class ReceiptTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String tenantId;  // Link the template to a specific tenant

    @Column(nullable = false)
    private String templateName;  // Name of the template for identification

    @OneToMany(mappedBy = "template", cascade = CascadeType.ALL)
    private List<TemplateField> fields;  // List of fields in the template

    // Getters and Setters
}
