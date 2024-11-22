package com.example.receipt_backend.controller;

import com.example.receipt_backend.dto.request.TenantRequestDTO;
import com.example.receipt_backend.dto.response.TenantResponseDTO;
import com.example.receipt_backend.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tenants")
public class TenantController {

    @Autowired
    private TenantService tenantService;

    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN')")
    @PostMapping
    public ResponseEntity<TenantResponseDTO> createTenant(@Validated @RequestBody TenantRequestDTO tenantRequest) {
        TenantResponseDTO createdTenant = tenantService.createTenant(tenantRequest);
        return new ResponseEntity<>(createdTenant, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ROLE_SYSTEM_ADMIN', 'ROLE_COMPANY_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<TenantResponseDTO> updateTenant(
            @PathVariable Long id,
            @Validated @RequestBody TenantRequestDTO tenantRequest) {
        TenantResponseDTO updatedTenant = tenantService.updateTenant(id, tenantRequest);
        return new ResponseEntity<>(updatedTenant, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTenant(@PathVariable Long id) {
        tenantService.deleteTenant(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasAnyRole('ROLE_SYSTEM_ADMIN', 'ROLE_COMPANY_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<TenantResponseDTO> getTenantById(@PathVariable Long id) {
        TenantResponseDTO tenant = tenantService.getTenantById(id);
        return new ResponseEntity<>(tenant, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN')")
    @GetMapping
    public ResponseEntity<List<TenantResponseDTO>> getAllTenants() {
        List<TenantResponseDTO> tenants = tenantService.getAllTenants();
        return new ResponseEntity<>(tenants, HttpStatus.OK);
    }
}
