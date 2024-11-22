package com.example.receipt_backend.service;

import com.example.receipt_backend.dto.request.TenantRequestDTO;
import com.example.receipt_backend.dto.response.TenantResponseDTO;

import java.util.List;

public interface TenantService {
    TenantResponseDTO createTenant(TenantRequestDTO tenantRequest);

    TenantResponseDTO updateTenant(Long id, TenantRequestDTO tenantRequest);

    void deleteTenant(Long id);

    TenantResponseDTO getTenantById(Long id);

    List<TenantResponseDTO> getAllTenants();
}
