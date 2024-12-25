// src/main/java/com/example/receipt_backend/entity/ReceiptType.java
package com.example.receipt_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "receipt_type")
@Getter
@Setter
public class ReceiptType {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "receipt_type_id")
    private UUID receiptTypeId;

    @Column(nullable = false, unique = true)
    private String name; // e.g., "Restaurant Receipt"

    @Column(nullable = false)
    private String description; // e.g., "Receipt from a restaurant"

    // Dynamic fields with their expected data types
    @ElementCollection
    @CollectionTable(name = "receipt_type_fields", joinColumns = @JoinColumn(name = "receipt_type_id"))
    @AttributeOverrides({
            @AttributeOverride(name = "fieldName", column = @Column(name = "field_name")),
            @AttributeOverride(name = "fieldType", column = @Column(name = "field_type"))
    })
    private List<ReceiptField> fields; // e.g., {"fieldName": "totalAmount", "fieldType": "number"}

}
