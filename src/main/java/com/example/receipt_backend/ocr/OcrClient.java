package com.example.receipt_backend.ocr;

import com.example.receipt_backend.dto.response.OcrResponse;
import com.example.receipt_backend.exception.BadRequestException;
import com.example.receipt_backend.service.FileStorageService;
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
import java.io.InputStream;
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
    private final FileStorageService s3Service;


    public OcrResponse processReceipt(String imagePath, String templateKey) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("test_image_path", imagePath);

        // Load additional data from the JSON template file in S3
        Map<String, Object> additionalData = parseJsonFileFromS3(templateKey);
        payload.putAll(additionalData);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<OcrResponse> responseEntity = restTemplate.postForEntity(ocrServiceBaseUrl +
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
     * Parses a JSON file from S3 into a Map.
     *
     * @param templateKey the S3 key of the JSON template file
     * @return a map representation of the JSON data
     */
    private Map<String, Object> parseJsonFileFromS3(String templateKey) {
        try (InputStream inputStream = s3Service.downloadFile(templateKey)) {
            return objectMapper.readValue(inputStream, new TypeReference<>() {});
        } catch (IOException e) {
            log.error("Failed to read JSON file from S3 with key: {}", templateKey, e);
            throw new BadRequestException("Failed to read JSON template file from S3.", e);
        }
    }
}
