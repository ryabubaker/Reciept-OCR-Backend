// src/main/java/com/example/receipt_backend/utils/MapToJsonConverter.java
package com.example.receipt_backend.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.Setter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Converter
public class MapToJsonConverter implements AttributeConverter<HashMap<String,Object>, String> {

    // Setter method to inject ObjectMapper
    // Static ObjectMapper to be set via AppUtils
    @Setter
    private static ObjectMapper objectMapper;

    // Public no-args constructor required by Hibernate
    public MapToJsonConverter() {
        // Default constructor
    }

    @Override
    public String convertToDatabaseColumn(HashMap attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return "{}";
        }
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting Map to JSON", e);
        }
    }

    @Override
    public HashMap<String, Object> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return new HashMap<>();
        }
        try {
            return objectMapper.readValue(dbData, new TypeReference<>() {});
        } catch (IOException e) {
            throw new IllegalArgumentException("Error converting JSON to Map", e);
        }
    }
    
}
