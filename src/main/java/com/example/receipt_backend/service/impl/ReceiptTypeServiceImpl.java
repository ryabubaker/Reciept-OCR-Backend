package com.example.receipt_backend.service.impl;

import com.example.receipt_backend.config.multitenant.CurrentTenantIdentifierResolverImpl;
import com.example.receipt_backend.dto.request.ReceiptTypeRequestDTO;
import com.example.receipt_backend.dto.request.ReceiptTypeUpdateRequestDTO;
import com.example.receipt_backend.dto.response.ReceiptTypeResponseDTO;
import com.example.receipt_backend.entity.ReceiptType;
import com.example.receipt_backend.exception.AppExceptionConstants;
import com.example.receipt_backend.exception.BadRequestException;
import com.example.receipt_backend.exception.ResourceNotFoundException;
import com.example.receipt_backend.mapper.ReceiptTypeMapper;
import com.example.receipt_backend.repository.ReceiptTypeRepository;
import com.example.receipt_backend.service.ReceiptTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReceiptTypeServiceImpl implements ReceiptTypeService {

    private final ReceiptTypeRepository receiptTypeRepository;
    private final ReceiptTypeMapper receiptTypeMapper;

    @Transactional
    @Override
    public ReceiptTypeResponseDTO createReceiptType(ReceiptTypeRequestDTO requestDTO) {
        String tenant = CurrentTenantIdentifierResolverImpl.getTenant();
        log.debug("Current Tenant is {}", tenant );
        if (tenant == null) {

            throw new RuntimeException("Tenant not set in context");
        }

        boolean exists = receiptTypeRepository.existsByName(requestDTO.getName());
        if (exists) {
            throw new BadRequestException(AppExceptionConstants.RECEIPT_TYPE_ALREADY_EXISTS);
        }

        // Map DTO to Entity
        ReceiptType receiptType = receiptTypeMapper.toEntity(requestDTO);

        // Save Entity; Hibernate will handle the tenant scoping
        ReceiptType savedReceiptType = receiptTypeRepository.save(receiptType);

        // Map Entity to Response DTO
        return receiptTypeMapper.toResponseDTO(savedReceiptType);
    }

    @Transactional(readOnly = true)
    @Override
    public ReceiptTypeResponseDTO getReceiptTypeById(UUID receiptTypeId) {
        ReceiptType receiptType = receiptTypeRepository.findById(receiptTypeId)
                .orElseThrow(() -> new ResourceNotFoundException(AppExceptionConstants.RECEIPT_TYPE_NOT_FOUND));

        return receiptTypeMapper.toResponseDTO(receiptType);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ReceiptTypeResponseDTO> getAllReceiptTypes() {
        List<ReceiptType> receiptTypes = receiptTypeRepository.findAll();
        return receiptTypes.stream()
                .map(receiptTypeMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ReceiptTypeResponseDTO updateReceiptType(UUID receiptTypeId, ReceiptTypeUpdateRequestDTO requestDTO) {
        ReceiptType receiptType = receiptTypeRepository.findById(receiptTypeId)
                .orElseThrow(() -> new ResourceNotFoundException(AppExceptionConstants.RECEIPT_TYPE_NOT_FOUND));

        // Update fields as per DTO
        receiptTypeMapper.updateEntity(requestDTO, receiptType);

        // Save updated entity
        ReceiptType updatedReceiptType = receiptTypeRepository.save(receiptType);

        return receiptTypeMapper.toResponseDTO(updatedReceiptType);
    }

    @Transactional
    @Override
    public void deleteReceiptType(UUID receiptTypeId) {
        ReceiptType receiptType = receiptTypeRepository.findById(receiptTypeId)
                .orElseThrow(() -> new ResourceNotFoundException(AppExceptionConstants.RECEIPT_TYPE_NOT_FOUND));

        receiptTypeRepository.delete(receiptType);
    }

    @Override
    @Transactional
    public void deleteReceiptField(UUID receiptTypeId, String fieldName) {
        ReceiptType receiptType = receiptTypeRepository.findById(receiptTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("ReceiptType not found with ID: " + receiptTypeId));

        boolean removed = receiptType.getFields().removeIf(field -> field.getFieldName().equalsIgnoreCase(fieldName));

        if (!removed) {
            throw new ResourceNotFoundException("Field '" + fieldName + "' not found in ReceiptType with ID: " + receiptTypeId);
        }

        receiptTypeRepository.save(receiptType);
    }
}
