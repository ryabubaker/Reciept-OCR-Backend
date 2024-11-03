package com.example.receipt_backend.entity;

import jakarta.persistence.*;


@Entity
public class TemplateField {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "template_id", nullable = false)
    private ReceiptTemplate template;

    @Column(nullable = false)
    private String fieldName;  // e.g., "Date", "Amount"

    @Column(nullable = false)
    private String fieldType;  // e.g., "DATE", "NUMBER", "STRING"

    @Column(nullable = false)
    private int x;  // X-coordinate on the image

    @Column(nullable = false)
    private int y;  // Y-coordinate on the image

    @Column(nullable = false)
    private int width;  // Width of the field region

    @Column(nullable = false)
    private int height;  // Height of the field region

    // Getters and Setters
}
