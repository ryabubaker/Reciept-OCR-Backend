package com.example.receipt_backend.service.impl;

import com.example.receipt_backend.dto.request.TenantRequestDTO;
import com.example.receipt_backend.dto.response.TenantResponseDTO;
import com.example.receipt_backend.entity.Tenant;
import com.example.receipt_backend.mapper.TenantMapper;
import com.example.receipt_backend.repository.TenantRepository;
import com.example.receipt_backend.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TenantServiceImpl implements TenantService {
    @Autowired
    private TenantSchemaServiceImpl tenantSchemaService;
    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private TenantMapper tenantMapper;

    @Override
    public TenantResponseDTO createTenant(TenantRequestDTO request) {
        Tenant tenant = tenantMapper.toEntity(request);

        tenantSchemaService.createTenantSchema(tenant.getTenantId());

        return tenantMapper.toDto(tenantRepository.save(tenant));

    }

    @Override
    public TenantResponseDTO updateTenant(Long id, TenantRequestDTO tenantRequest) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));
        tenant.setCompanyName(tenantRequest.getCompanyName());
        return tenantMapper.toDto(tenantRepository.save(tenant));
    }

    @Override
    public void deleteTenant(Long id) {
        tenantRepository.deleteById(id);
    }

    @Override
    public TenantResponseDTO getTenantById(Long id) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));
        return tenantMapper.toDto(tenant);
    }

    @Override
    public List<TenantResponseDTO> getAllTenants() {
        return tenantRepository.findAll().stream()
                .map(tenantMapper::toDto)
                .collect(Collectors.toList());
    }
}
