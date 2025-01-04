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
import com.example.receipt_backend.service.ReceiptTypeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.*;
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
    private final ObjectMapper objectMapper;

    @Value("${myapp.template.base-directory}")
    private String baseDirectory;

    @Transactional
    @Override
    public ReceiptTypeResponseDTO createReceiptType(ReceiptTypeRequestDTO requestDTO) throws IOException {
        // Validate file
        Path targetLocation = saveTemplateAsFile(requestDTO.getName(), requestDTO.getTemplate());

        try {
            // Create and save ReceiptType entity
            ReceiptType receiptType = ReceiptType.builder()
                    .name(requestDTO.getName())
                    .templatePath(targetLocation.toString())
                    .build();
            receiptTypeRepository.save(receiptType);

            // Map to response DTO
            ReceiptTypeResponseDTO responseDTO = receiptTypeMapper.toResponseDTO(receiptType);
            return responseDTO;
        } catch (Exception e) {
            Files.deleteIfExists(targetLocation);
            throw e;
        }
    }

    private Path saveTemplateAsFile(String name, Map<String, Object> template) throws IOException {
        if (template == null || template.isEmpty()) {
            throw new CustomAppException("Template data is required.");
        }

        // Define the target location
        String tenant = CurrentTenantIdentifierResolverImpl.getTenant();
        Path tenantDirectory = Paths.get(baseDirectory, tenant);
        Path targetLocation = tenantDirectory.resolve(name + ".json");

        // Create directories if they don't exist
        if (!Files.exists(tenantDirectory)) {
            Files.createDirectories(tenantDirectory);
        }

        // Save the file
        Files.writeString(targetLocation, objectMapper.writeValueAsString(template), StandardOpenOption.CREATE);
        return targetLocation;
    }

    @Override
    public ReceiptTypeResponseDTO updateReceiptType(String currentReceiptTypeName, ReceiptTypeUpdateRequestDTO updateDto) throws IOException {
        ReceiptType existing = receiptTypeRepository.findByName(currentReceiptTypeName)
                .orElseThrow(() -> new ResourceNotFoundException(AppExceptionConstants.RECEIPT_TYPE_NOT_FOUND));

        try {
            // Update the name if provided
            if (updateDto.getName() != null && !updateDto.getName().isBlank()) {
                existing.setName(updateDto.getName());
            }

            // Update the template if provided
            if (updateDto.getTemplate() != null && !updateDto.getTemplate().isEmpty()) {
                Path targetLocation = saveTemplateAsFile(existing.getName(), updateDto.getTemplate());
                existing.setTemplatePath(targetLocation.toString());
            }

            receiptTypeRepository.save(existing);

            return receiptTypeMapper.toResponseDTO(existing);
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

        return receiptTypeMapper.toResponseDTO(receiptType);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Map<String, ? extends Serializable>> getAllReceiptTypes() {
        List<ReceiptType> receiptTypes = receiptTypeRepository.findAll();
        List<Map<String, ? extends Serializable>> collect = receiptTypes.stream()
                .map(receiptType ->
                        Map.of(
                                "receiptTypeId", receiptType.getReceiptTypeId().toString(),
                                "name", receiptType.getName()))
                .collect(Collectors.toList());

        return collect;
    }

    @Transactional
    @Override
    public void deleteReceiptType(String receiptTypeId) throws IOException {

        ReceiptType receiptType = receiptTypeRepository.findById(UUID.fromString(receiptTypeId))
                .orElseThrow(() -> new ResourceNotFoundException(AppExceptionConstants.RECEIPT_TYPE_NOT_FOUND));

        // Delete the template file from path
        String templatePath = receiptType.getTemplatePath();

        Files.deleteIfExists(Path.of(templatePath));

        // Delete the entity
        receiptTypeRepository.delete(receiptType);
    }
}
