package com.example.receipt_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Entity
@Table(name = "receipt")
@Getter
@Setter
public class Receipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long receiptId;

    @ManyToOne
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;  // Receipt belongs to a company

    @ManyToOne
    @JoinColumn(name = "receipt_format_id", nullable = false)
    private ReceiptType receiptType;  // Receipt follows a specific format

    private String receiptImageUrl;  // URL of the uploaded image

    @Column(name = "uploaded_at")
    private String uploadedAt;

    // OCR data stored as a JSON map, could include dynamic keys (e.g., "totalAmount", "vendorName")
    @ElementCollection
    @CollectionTable(name = "ocr_data", joinColumns = @JoinColumn(name = "receipt_id"))
    @MapKeyColumn(name = "data_key")
    @Column(name = "data_value")
    private Map<String, String> ocrData;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;  // User who uploaded the receipt
}

