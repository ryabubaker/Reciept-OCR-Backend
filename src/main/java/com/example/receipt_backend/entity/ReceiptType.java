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
    private HashMap<String, Integer> column2idxMap;

    // Method to extract column2idxMap from a template JSON

    public static HashMap<String, Integer> extractColumn2IdxMap(Map<String, Object> template) {
        if (template == null || !template.containsKey("shapes")) {
            throw new IllegalArgumentException("Invalid template: missing 'shapes' key");
        }

        HashMap<String, Integer> column2idxMap = new HashMap<>();
        List<Map<String, Object>> shapes = (List<Map<String, Object>>) template.get("shapes");

        for (Map<String, Object> shape : shapes) {
            String label = (String) shape.get("label");
            Integer id = (Integer) shape.get("id");
            column2idxMap.put(label, id);
        }

        return column2idxMap;
    }


}




