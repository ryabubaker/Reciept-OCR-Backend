package com.example.receipt_backend.service.impl;

import com.example.receipt_backend.config.multitenant.CurrentTenantIdentifierResolverImpl;
import com.example.receipt_backend.dto.request.ReceiptTypeRequestDTO;
import com.example.receipt_backend.dto.request.ReceiptTypeUpdateRequestDTO;
import com.example.receipt_backend.dto.response.GenericResponseDTO;
import com.example.receipt_backend.dto.response.ReceiptTypeResponseDTO;
import com.example.receipt_backend.entity.ReceiptType;
import com.example.receipt_backend.exception.CustomAppException;
import com.example.receipt_backend.exception.ErrorCode;
import com.example.receipt_backend.exception.ResourceNotFoundException;
import com.example.receipt_backend.mapper.ReceiptTypeMapper;
import com.example.receipt_backend.repository.ReceiptTypeRepository;
import com.example.receipt_backend.service.FileStorageService;
import com.example.receipt_backend.service.ReceiptTypeService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReceiptTypeServiceImpl implements ReceiptTypeService {

    private final ReceiptTypeRepository receiptTypeRepository;
    private final ReceiptTypeMapper receiptTypeMapper;
    private final FileStorageService storageService;

    @Transactional
    @Override
    public ReceiptTypeResponseDTO createReceiptType(ReceiptTypeRequestDTO requestDTO) throws IOException {
        // Validate that a template is provided
        if (requestDTO.getTemplate() == null || requestDTO.getTemplate().isEmpty()) {
            throw new CustomAppException(
                    ErrorCode.MISSING_REQUIRED_FIELD,
                    "Missing template."
            );
        }
    
        // Check if a ReceiptType with the same name already exists
        boolean exists = receiptTypeRepository.existsByName(requestDTO.getName());
        if (exists) {
            throw new CustomAppException(
                    ErrorCode.RECEIPT_TYPE_AlREADY_EXISTS);
        }
    
        // Convert the template map to a file and upload
        String key = saveTemplateAsFile(requestDTO.getName(), requestDTO.getTemplate());
    
        try {
            // Also read the uploaded template back for the response
            Map<String, Object> map = readTemplate(key);
    
            // Create and save ReceiptType entity
            ReceiptType receiptType = ReceiptType.builder()
                    .name(requestDTO.getName())
                    .templatePath(key)
                    .column2idxMap(requestDTO.getColumn2idxMap())
                    .build();
    
            receiptTypeRepository.save(receiptType);
    
            // Convert entity to response DTO
            ReceiptTypeResponseDTO responseDTO = receiptTypeMapper.toResponseDTO(receiptType);
            responseDTO.setTemplate(map);
            responseDTO.setColumn2idxMap(requestDTO.getColumn2idxMap());
            return responseDTO;
    
        } catch (Exception e) {
            log.error("Failed to upload template: {}", e.getMessage(), e);
            throw new CustomAppException(
                    ErrorCode.FILE_UPLOAD_FAILED,
                    "Failed to create new receipt type"
            );
        }
    }

    @Override
    public ReceiptTypeResponseDTO updateReceiptType(String receiptTypeId, ReceiptTypeRequestDTO updateDto)
            throws IOException {

        ReceiptType existing = receiptTypeRepository.findById(UUID.fromString(receiptTypeId))
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.RECEIPT_TYPE_NOT_FOUND
                ));

        try {
            // Update the name if provided
            if (updateDto.getName() != null && !updateDto.getName().isBlank()) {
                existing.setName(updateDto.getName());
            }

            // Update the template if provided
            if (updateDto.getTemplate() != null && !updateDto.getTemplate().isEmpty()) {
                // Save or replace the template file
                String key = saveTemplateAsFile(existing.getName(), updateDto.getTemplate());
                existing.setTemplatePath(key);

                // Update the column2idxMap
                HashMap<String, Integer> column2idxMap = updateDto.getColumn2idxMap();
                existing.setColumn2idxMap(column2idxMap);
            }

            receiptTypeRepository.save(existing);
            Map<String, Object> map = readTemplate(existing.getTemplatePath());

            ReceiptTypeResponseDTO responseDTO = receiptTypeMapper.toResponseDTO(existing);
            responseDTO.setTemplate(map);
            responseDTO.setColumn2idxMap(updateDto.getColumn2idxMap());
            return responseDTO;

        } catch (Exception e) {
            log.error("Error updating receipt type: {}", e.getMessage(), e);
            throw new CustomAppException(
                    ErrorCode.BAD_REQUEST,
                    "Error updating receipt type: " + e.getMessage()
            );
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ReceiptTypeResponseDTO getReceiptTypeById(String receiptTypeId) {
        ReceiptType receiptType = receiptTypeRepository.findById(UUID.fromString(receiptTypeId))
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.RECEIPT_TYPE_NOT_FOUND
                ));

        String key = receiptType.getTemplatePath();
        Map<String, Object> map = readTemplate(key);

        ReceiptTypeResponseDTO responseDTO = receiptTypeMapper.toResponseDTO(receiptType);
        responseDTO.setTemplate(map);
        responseDTO.setColumn2idxMap(receiptType.getColumn2idxMap());
        return responseDTO;
    }

    @Override
    public List<Map<String, Object>> getAllReceiptTypes() {
        List<ReceiptType> receiptTypes = receiptTypeRepository.findAll();
        List<Map<String, Object>> collect = receiptTypes.stream().map(receiptType -> {
            Map<String, Object> map = Map.of(
                "id", receiptType.getReceiptTypeId().toString(),
                "name", receiptType.getName()
            );
            return map;
        }).collect(Collectors.toList());
        return collect;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ReceiptTypeResponseDTO> getAllReceiptTypesWithJson() {
        return receiptTypeRepository.findAll().stream().map(receiptType -> {
            String key = receiptType.getTemplatePath();
            Map<String, Object> map = readTemplate(key);

            ReceiptTypeResponseDTO responseDTO = receiptTypeMapper.toResponseDTO(receiptType);
            Map<String, Integer> columnMap = receiptType.getColumn2idxMap(); // Ensure this is fetched
            responseDTO.setColumn2idxMap(new HashMap<>(columnMap)); // Forces initialization

            responseDTO.setTemplate(map);
            return responseDTO;
        }).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public GenericResponseDTO<Boolean> deleteReceiptType(String receiptTypeId) throws IOException {
        ReceiptType receiptType = receiptTypeRepository.findById(UUID.fromString(receiptTypeId))
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.RECEIPT_TYPE_NOT_FOUND
                ));

        // Delete the template file from S3
        String templatePath = receiptType.getTemplatePath();
        storageService.deleteFile(templatePath);

        // Delete the entity
        receiptTypeRepository.delete(receiptType);

        return GenericResponseDTO.<Boolean>builder()
                .response(true)
                .build();
    }

    /**
     * Saves the given template to a JSON file in S3.
     */
    private String saveTemplateAsFile(String name, Map<String, Object> template) throws IOException {
        if (template == null || template.isEmpty()) {
            throw new CustomAppException(
                    ErrorCode.MISSING_REQUIRED_FIELD,
                    "Template data is required."
            );
        }

        // Construct a filename for S3
        String filename = name + ".json";
        String tenantName = CurrentTenantIdentifierResolverImpl.getTenant();
        String key = String.format("templates/%s/%s", tenantName, filename);

        // Convert the template map to JSON
        byte[] jsonBytes = new ObjectMapper().writeValueAsBytes(template);
        long contentLength = jsonBytes.length;
        String contentType = "application/json";

        // Upload to S3
        try (InputStream inputStream = new ByteArrayInputStream(jsonBytes)) {
            storageService.uploadTemplate(key, inputStream, contentLength, contentType);
        }
        return key;
    }

    /**
     * Reads the template JSON from the given S3 key and converts it back to a Map.
     */
    private Map<String, Object> readTemplate(String templateKey) {
        try (InputStream inputStream = storageService.downloadFile(templateKey)) {
            return new ObjectMapper().readValue(inputStream, new TypeReference<Map<String, Object>>() {});
        } catch (IOException e) {
            log.error("Failed to read template from S3: {}", templateKey, e);
            throw new CustomAppException(
                    ErrorCode.FILE_DOWNLOAD_FAILED,
                    "Failed to read template from S3: " + templateKey
            );
        }
    }
}
