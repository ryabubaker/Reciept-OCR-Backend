package com.example.receipt_backend.ocr;

import com.example.receipt_backend.dto.response.OcrResponse;
import com.example.receipt_backend.exception.BadRequestException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class OcrClient {
    @Value("${myapp.ocr.base-url}")
    private String ocrServiceBaseUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    /**
     * Processes a receipt by sending a POST request to the OCR service.
     *
     * @param imagePath    the path to the image
     * @param templatePath the path to the JSON template
     * @return the OCR response
     */
    public OcrResponse processReceipt(String imagePath, String templatePath) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("test_image_path", imagePath);

        // Load additional data from the JSON template file
        Map<String, Object> additionalData = parseJsonFile(templatePath);
        payload.putAll(additionalData);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<OcrResponse> responseEntity = restTemplate.postForEntity( ocrServiceBaseUrl +
                    "/process", requestEntity, OcrResponse.class);

            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                return responseEntity.getBody();
            } else {
                log.error("OCR service responded with status: {}", responseEntity.getStatusCode());
                throw new BadRequestException("OCR service failed with status: " + responseEntity.getStatusCode());
            }
        } catch (HttpStatusCodeException ex) {
            String errorBody = ex.getResponseBodyAsString();
            log.error("OCR service responded with error: {}", errorBody, ex);
            throw new BadRequestException("OCR service failed: " + errorBody, ex);
        } catch (Exception e) {
            log.error("Failed to process receipt with OCR service.", e);
            throw new BadRequestException("Failed to process receipt with OCR service.", e);
        }
    }

    /**
     * Parses a JSON file into a Map.
     *
     * @param jsonFilePath the path to the JSON file
     * @return a map representation of the JSON data
     */
    private Map<String, Object> parseJsonFile(String jsonFilePath) {
        try {
            return objectMapper.readValue(new File(jsonFilePath), new TypeReference<>() {});
        } catch (IOException e) {
            log.error("Failed to read JSON file at path: {}", jsonFilePath, e);
            throw new BadRequestException("Failed to read JSON template file.", e);
        }
    }
}
