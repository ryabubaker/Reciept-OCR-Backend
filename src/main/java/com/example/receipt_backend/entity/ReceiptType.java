// src/main/java/com/example/receipt_backend/entity/ReceiptType.java
package com.example.receipt_backend.entity;

import jakarta.persistence.*;
import lombok.*;

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
    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @Column(name = "template_path")
    private String templatePath;


}

