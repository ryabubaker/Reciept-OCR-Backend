package com.example.receipt_backend.service;

import com.example.receipt_backend.dto.UserDTO;
import com.example.receipt_backend.dto.request.TenantRequestDTO;
import com.example.receipt_backend.dto.request.UpdateTenantRequestDTO;
import com.example.receipt_backend.dto.response.TenantResponseDTO;
import com.example.receipt_backend.entity.Tenant;
import com.example.receipt_backend.entity.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface TenantService {
    TenantResponseDTO createTenant(TenantRequestDTO tenantRequest);

    //User createAdminUser(TenantRequestDTO request, Tenant tenant);

    @Transactional
    TenantResponseDTO updateTenant(UUID tenantId, UpdateTenantRequestDTO request);

    @Transactional
    void inactivateTenant(UUID tenantId);

    void deleteTenant(UUID id);

    TenantResponseDTO getTenantById(UUID id);

    List<TenantResponseDTO> getAllTenants();

    void activateTenant(UUID id);

    @Transactional(readOnly = true)
    List<UserDTO> getUsersByTenantId(UUID tenantId);
}
