package com.example.receipt_backend.service.impl;

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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReceiptTypeServiceImpl implements ReceiptTypeService {

    private final ReceiptTypeRepository receiptTypeRepository;
    private final ReceiptTypeMapper receiptTypeMapper;

    @Value("${myapp.template.base-directory}")
    private String baseDirectory;

    @Transactional
    @Override
    public ReceiptTypeResponseDTO createReceiptType(ReceiptTypeRequestDTO requestDTO) throws IOException {
        // Validate file
        MultipartFile templateFile = requestDTO.getTemplate();
        Path targetLocation = saveTemplate(templateFile);

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

    private Path saveTemplate(MultipartFile templateFile) throws IOException {
        if (templateFile == null || templateFile.isEmpty()) {
            throw new CustomAppException("Template file is required.");
        }
        if (!templateFile.getOriginalFilename().endsWith(".json")) {
            throw new CustomAppException("Only .json files are allowed.");
        }

        // Define the target location
        String originalFilename = StringUtils.cleanPath(templateFile.getOriginalFilename());
        Path tenantDirectory = Paths.get(baseDirectory, "Tenant_A"); // Adjust for multi-tenant
        Path targetLocation = tenantDirectory.resolve(originalFilename);

        // Create directories if they don't exist
        if (!Files.exists(tenantDirectory)) {
            Files.createDirectories(tenantDirectory);
        }

        // Save the file
        Files.copy(templateFile.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        return targetLocation;
    }

    @Override
    public ReceiptTypeResponseDTO updateReceiptType(String currentReceiptTypeName, ReceiptTypeUpdateRequestDTO updateDto) throws IOException {
        ReceiptType existing = receiptTypeRepository.findByName(currentReceiptTypeName)
                .orElseThrow(() -> new ResourceNotFoundException(AppExceptionConstants.RECEIPT_TYPE_NOT_FOUND));

        MultipartFile newJsonFile = updateDto.getTemplate();
        saveTemplate(newJsonFile);

        receiptTypeMapper.updateEntity(updateDto, existing);

        return receiptTypeMapper.toResponseDTO(existing);
    }

    @Transactional(readOnly = true)
    @Override
    public ReceiptTypeResponseDTO getReceiptTypeByName(String receiptTypeName) {
        ReceiptType receiptType = receiptTypeRepository.findByName(receiptTypeName)
                .orElseThrow(() -> new ResourceNotFoundException(AppExceptionConstants.RECEIPT_TYPE_NOT_FOUND));

        return receiptTypeMapper.toResponseDTO(receiptType);
    }

    @Transactional(readOnly = true)
    @Override
    public List<String> getAllReceiptTypes() {
        List<ReceiptType> receiptTypes = receiptTypeRepository.findAll();
        return receiptTypes.stream()
                .map(ReceiptType::getName)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void deleteReceiptType(String receiptTypeName) throws IOException {

        ReceiptType receiptType = receiptTypeRepository.findByName(receiptTypeName)
                .orElseThrow(() -> new ResourceNotFoundException(AppExceptionConstants.RECEIPT_TYPE_NOT_FOUND));

        // Delete the template file from path
        String templatePath = receiptType.getTemplatePath();

        Files.deleteIfExists(Path.of(templatePath));

        // Delete the entity
        receiptTypeRepository.delete(receiptType);
    }
}
