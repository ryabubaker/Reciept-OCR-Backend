package com.example.receipt_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "receipt_format")
@Getter
@Setter
public class ReceiptType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long formatId;

    private String name; // "Type A", "Type B", etc.
    private String description; // A description of this receipt type, e.g., "Restaurant Receipt"


    @ManyToOne
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;  // Each format is linked to a tenant


    // List of dynamic fields for each format (e.g., "totalAmount", "tax")
    @ElementCollection
    private List<String> fields;  // Store dynamic fields (this could be JSON as well)
}

