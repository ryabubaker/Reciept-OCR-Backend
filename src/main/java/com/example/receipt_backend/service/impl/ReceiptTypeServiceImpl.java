package com.example.receipt_backend.service.impl;

import com.example.receipt_backend.config.multitenant.CurrentTenantIdentifierResolverImpl;
import com.example.receipt_backend.dto.request.ReceiptTypeRequestDTO;
import com.example.receipt_backend.dto.request.ReceiptTypeUpdateRequestDTO;
import com.example.receipt_backend.dto.response.ReceiptTypeResponseDTO;
import com.example.receipt_backend.entity.ReceiptType;
import com.example.receipt_backend.exception.AppExceptionConstants;
import com.example.receipt_backend.exception.CustomAppException;
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
        // Validate file
        String key = saveTemplateAsFile(requestDTO.getName(), requestDTO.getTemplate());

        try {
            // Use the ReceiptType method to extract column2idxMap
            Map<String, Integer> column2idxMap = ReceiptType.extractColumn2IdxMap(requestDTO.getTemplate());
            Map<String, Object> map = readTemplate(key);

            // Create and save ReceiptType entity
            ReceiptType receiptType = ReceiptType.builder()
                    .name(requestDTO.getName())
                    .templatePath(key)
                    .column2idxMap(column2idxMap)
                    .build();
            receiptTypeRepository.save(receiptType);

            // Map to response DTO
            ReceiptTypeResponseDTO responseDTO = receiptTypeMapper.toResponseDTO(receiptType);
            responseDTO.setTemplate(map);
            return responseDTO;
        } catch (Exception e) {
            log.error("Failed to upload template to S3: {}", e.getMessage());
            throw new RuntimeException("Failed to upload template to S3", e);
        }
    }

    @Override
    public ReceiptTypeResponseDTO updateReceiptType(String receiptTypeId, ReceiptTypeUpdateRequestDTO updateDto) throws IOException {
        ReceiptType existing = receiptTypeRepository.findById(UUID.fromString(receiptTypeId))
                .orElseThrow(() -> new ResourceNotFoundException(AppExceptionConstants.RECEIPT_TYPE_NOT_FOUND));
        String key = saveTemplateAsFile(existing.getName(), updateDto.getTemplate());


        try {
            // Update the name if provided
            if (updateDto.getName() != null && !updateDto.getName().isBlank()) {
                existing.setName(updateDto.getName());
            }

            // Update the template if provided
            if (updateDto.getTemplate() != null && !updateDto.getTemplate().isEmpty()) {
                existing.setTemplatePath(key);

                // Update the column2idxMap
                Map<String, Integer> column2idxMap = ReceiptType.extractColumn2IdxMap(updateDto.getTemplate());
                existing.setColumn2idxMap(column2idxMap);
            }

            receiptTypeRepository.save(existing);
            Map<String, Object> map = readTemplate(key);

            ReceiptTypeResponseDTO responseDTO = receiptTypeMapper.toResponseDTO(existing);
            responseDTO.setTemplate(map);
            return responseDTO;
        } catch (Exception e) {
            log.error("Error updating receipt type: {}", e.getMessage());
            throw new RuntimeException("Failed to update receipt type: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ReceiptTypeResponseDTO getReceiptTypeById(String receiptTypeId) {
        ReceiptType receiptType = receiptTypeRepository.findById(UUID.fromString(receiptTypeId))
                .orElseThrow(() -> new ResourceNotFoundException(AppExceptionConstants.RECEIPT_TYPE_NOT_FOUND));
        String key = receiptType.getTemplatePath();
        Map<String, Object> map = readTemplate(key);
        ReceiptTypeResponseDTO responseDTO = receiptTypeMapper.toResponseDTO(receiptType);
        responseDTO.setTemplate(map);
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
        List<ReceiptType> receiptTypes = receiptTypeRepository.findAll();
        List<ReceiptTypeResponseDTO> collect = receiptTypes.stream().map(receiptType -> {
            String key = receiptType.getTemplatePath();
            Map<String, Object> map = readTemplate(key);
            ReceiptTypeResponseDTO responseDTO = receiptTypeMapper.toResponseDTO(receiptType);
            responseDTO.setTemplate(map);
            return responseDTO;
        }).collect(Collectors.toList());
    
        return collect;
    }
    
    

    @Transactional
    @Override
    public void deleteReceiptType(String receiptTypeId) throws IOException {

        ReceiptType receiptType = receiptTypeRepository.findById(UUID.fromString(receiptTypeId))
                .orElseThrow(() -> new ResourceNotFoundException(AppExceptionConstants.RECEIPT_TYPE_NOT_FOUND));

        // Delete the template file from path
        String templatePath = receiptType.getTemplatePath();

        storageService.deleteFile(templatePath);

        // Delete the entity
        receiptTypeRepository.delete(receiptType);
    }

    private String saveTemplateAsFile(String name, Map<String, Object> template) throws IOException {
        if (template == null || template.isEmpty()) {
            throw new CustomAppException("Template data is required.");
        }
        // Define the target location
        String filename = name + ".json";
        String tenantName = CurrentTenantIdentifierResolverImpl.getTenant();
        String key = String.format("templates/%s/%s", tenantName, filename);

        // Convert the template map to JSON bytes
        byte[] jsonBytes = new ObjectMapper().writeValueAsBytes(template);
        InputStream inputStream = new ByteArrayInputStream(jsonBytes);
        long contentLength = jsonBytes.length;
        String contentType = "application/json";

        // Upload the template to S3
        storageService.uploadTemplate(key, inputStream, contentLength, contentType);

        return key;
    }

    private  Map<String, Object> readTemplate(String templateKey) {

        try (InputStream inputStream = storageService.downloadFile(templateKey)) {
            return new ObjectMapper().readValue(inputStream, new TypeReference<Map<String, Object>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Failed to read template from S3: " + templateKey, e);
        }
    }
}
