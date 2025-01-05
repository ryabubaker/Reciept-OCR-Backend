// src/main/java/com/example/receipt_backend/entity/ReceiptType.java
package com.example.receipt_backend.entity;

import com.example.receipt_backend.config.multitenant.CurrentTenantIdentifierResolverImpl;
import com.example.receipt_backend.exception.CustomAppException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
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
    private Map<String, Integer> column2idxMap;

    // Method to extract column2idxMap from a template JSON

    public static Map<String, Integer> extractColumn2IdxMap(Map<String, Object> template) {
        if (template == null || !template.containsKey("shapes")) {
            throw new IllegalArgumentException("Invalid template: missing 'shapes' key");
        }

        Map<String, Integer> column2idxMap = new HashMap<>();
        List<Map<String, Object>> shapes = (List<Map<String, Object>>) template.get("shapes");

        for (Map<String, Object> shape : shapes) {
            String label = (String) shape.get("label");
            Integer id = (Integer) shape.get("id");
            column2idxMap.put(label, id);
        }

        return column2idxMap;
    }

    public static Path saveTemplateAsFile(String name, Map<String, Object> template) throws IOException {
        if (template == null || template.isEmpty()) {
            throw new CustomAppException("Template data is required.");
        }
        String baseDirectory = System.getenv("{myapp.template.base-directory");

        // Define the target location
        String tenant = CurrentTenantIdentifierResolverImpl.getTenant();
        Path tenantDirectory = Paths.get(baseDirectory, tenant);
        Path targetLocation = tenantDirectory.resolve(name + ".json");

        // Create directories if they don't exist
        if (!Files.exists(tenantDirectory)) {
            Files.createDirectories(tenantDirectory);
        }

        // Save the file
        Files.writeString(targetLocation, new ObjectMapper().writeValueAsString(template), StandardOpenOption.CREATE);
        return targetLocation;
    }

}




