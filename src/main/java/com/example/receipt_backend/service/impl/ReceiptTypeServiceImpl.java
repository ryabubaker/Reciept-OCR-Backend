package com.example.receipt_backend.service.impl;

import com.example.receipt_backend.dto.request.ReceiptTypeRequestDTO;
import com.example.receipt_backend.dto.request.ReceiptTypeUpdateRequestDTO;
import com.example.receipt_backend.dto.response.ReceiptTypeResponseDTO;
import com.example.receipt_backend.entity.ReceiptType;
import com.example.receipt_backend.exception.AppExceptionConstants;
import com.example.receipt_backend.exception.ResourceNotFoundException;
import com.example.receipt_backend.mapper.ReceiptTypeMapper;
import com.example.receipt_backend.repository.ReceiptTypeRepository;
import com.example.receipt_backend.service.ReceiptTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.example.receipt_backend.entity.ReceiptType.saveTemplateAsFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReceiptTypeServiceImpl implements ReceiptTypeService {

    private final ReceiptTypeRepository receiptTypeRepository;
    private final ReceiptTypeMapper receiptTypeMapper;


    @Transactional
    @Override
    public ReceiptTypeResponseDTO createReceiptType(ReceiptTypeRequestDTO requestDTO) throws IOException {
        // Validate file
        Path targetLocation = saveTemplateAsFile(requestDTO.getName(), requestDTO.getTemplate());

        try {
            // Use the ReceiptType method to extract column2idxMap
            Map<String, Integer> column2idxMap = ReceiptType.extractColumn2IdxMap(requestDTO.getTemplate());

            // Create and save ReceiptType entity
            ReceiptType receiptType = ReceiptType.builder()
                    .name(requestDTO.getName())
                    .templatePath(targetLocation.toString())
                    .column2idxMap(column2idxMap)
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

    @Override
    public ReceiptTypeResponseDTO updateReceiptType(String receiptTypeId, ReceiptTypeUpdateRequestDTO updateDto) throws IOException {
        ReceiptType existing = receiptTypeRepository.findById(UUID.fromString(receiptTypeId))
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

                // Update the column2idxMap
                Map<String, Integer> column2idxMap = ReceiptType.extractColumn2IdxMap(updateDto.getTemplate());
                existing.setColumn2idxMap(column2idxMap);
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
    public List<ReceiptTypeResponseDTO> getAllReceiptTypes() {
        List<ReceiptType> receiptTypes = receiptTypeRepository.findAll();
        List<ReceiptTypeResponseDTO> collect = receiptTypes.stream()
                .map(receiptTypeMapper::toResponseDTO)
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
