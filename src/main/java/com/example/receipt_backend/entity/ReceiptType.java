// src/main/java/com/example/receipt_backend/entity/ReceiptType.java
package com.example.receipt_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Table(name = "receipt_type")
@Data
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReceiptType {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "receipt_type_id", unique = true, nullable = false)
    private UUID receiptTypeId;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @Column(name = "template_path")
    private String templatePath;

    @ElementCollection
    @CollectionTable(name = "receipt_type_fields", joinColumns = @JoinColumn(name = "receipt_type_id"))
    @MapKeyColumn(name = "column_name")
    @Column(name = "column_index")
    private Map<String, Integer> column2idxMap = new HashMap<>();




}




