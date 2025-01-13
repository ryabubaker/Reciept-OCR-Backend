package com.example.receipt_backend.service.impl;

import com.example.receipt_backend.dto.UserDTO;
import com.example.receipt_backend.dto.request.TenantRequestDTO;
import com.example.receipt_backend.dto.request.UpdateTenantRequestDTO;
import com.example.receipt_backend.dto.response.TenantResponseDTO;
import com.example.receipt_backend.entity.RoleEntity;
import com.example.receipt_backend.entity.Tenant;
import com.example.receipt_backend.entity.User;
import com.example.receipt_backend.exception.CustomAppException;
import com.example.receipt_backend.exception.ErrorCode;
import com.example.receipt_backend.exception.ResourceNotFoundException;
import com.example.receipt_backend.mail.EmailService;
import com.example.receipt_backend.mapper.TenantMapper;
import com.example.receipt_backend.mapper.UserMapper;
import com.example.receipt_backend.repository.TenantRepository;
import com.example.receipt_backend.repository.UserRepository;
import com.example.receipt_backend.security.SecurityEnums;
import com.example.receipt_backend.service.RoleService;
import com.example.receipt_backend.service.TenantSchemaService;
import com.example.receipt_backend.service.TenantService;
import com.example.receipt_backend.service.UserService;
import com.example.receipt_backend.utils.AppUtils;
import com.example.receipt_backend.utils.RoleType;
import com.example.receipt_backend.utils.TenantStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TenantServiceImpl implements TenantService {

    private final TenantSchemaService tenantSchemaService;
    private final TenantRepository tenantRepository;
    private final TenantMapper tenantMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public TenantResponseDTO createTenant(String tenantName) {
        // Check if tenant name already exists
        if (tenantRepository.existsByTenantName(tenantName)) {
            throw new CustomAppException(
                    ErrorCode.TENANT_ALREADY_EXISTS,
                    ErrorCode.TENANT_ALREADY_EXISTS.getMessage()
            );
        }

        // Create the tenant schema in the database
        tenantSchemaService.createTenantSchema(tenantName);

        // Create tenant entity
        Tenant tenant = new Tenant();
        tenant.setTenantName(tenantName);

        // Save the tenant to generate a tenantId
        Tenant savedTenant = tenantRepository.save(tenant);

        return tenantMapper.toDto(savedTenant);
    }

    @Override
    @Transactional
    public void inactivateTenant(UUID tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.TENANT_NOT_FOUND)
                );

        tenant.setStatus(TenantStatus.INACTIVE);
        tenantRepository.save(tenant);
    }

    @Override
    @Transactional
    public void activateTenant(UUID tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.TENANT_NOT_FOUND)
                );

        tenant.setStatus(TenantStatus.ACTIVE);
        tenantRepository.save(tenant);
    }

    @Override
    @Transactional
    public void deleteTenant(UUID tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.TENANT_NOT_FOUND));

        try {
            tenantSchemaService.deleteTenantSchema(tenant.getTenantName());
        } catch (CustomAppException e) {
            e.printStackTrace();
            tenantRepository.delete(tenant);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public TenantResponseDTO getTenantById(UUID id) {
        Tenant tenant = getTenant(id);
        return tenantMapper.toDto(tenant);
    }

    private Tenant getTenant(UUID id) {
        return tenantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.TENANT_NOT_FOUND)
                );
    }

    @Override
    @Transactional(readOnly = true)
    public List<TenantResponseDTO> getAllTenants() {
        return tenantRepository.findAll().stream()
                .map(tenantMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getUsersByTenantId(UUID tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.TENANT_NOT_FOUND));

        return tenant.getUsers().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }
}
