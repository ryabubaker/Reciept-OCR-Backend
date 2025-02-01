// src/main/java/com/example/receipt_backend/dto/ReceiptDTO.java
package com.example.receipt_backend.dto;

import com.example.receipt_backend.utils.ReceiptStatus;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class ReceiptDTO {
    private String requestId;
    private String receiptId;
    private String receiptTypeId;
    private String receiptTypeName;
    private String imageUrl;
    private ReceiptStatus status;
    private Map<Integer, String> ocrData = new HashMap<>();
    private String approvedBy;
    private String approvedAt;

    // Handle JSON key conversion during deserialization
    @JsonAnySetter
    public void addOcrData(String key, String value) {
        try {
            this.ocrData.put(Integer.parseInt(key), value);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid key in OCR data: " + key);
        }
    }

    // Convert Integer keys back to String during serialization
    @JsonGetter("ocrData")
    public Map<String, String> getOcrDataAsString() {
        Map<String, String> convertedMap = new HashMap<>();
        for (Map.Entry<Integer, String> entry : ocrData.entrySet()) {
            convertedMap.put(String.valueOf(entry.getKey()), entry.getValue());
        }
        return convertedMap;
    }
}
