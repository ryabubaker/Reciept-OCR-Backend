package com.example.receipt_backend.controller;

import com.example.receipt_backend.dto.request.TenantRequestDTO;
import com.example.receipt_backend.dto.request.UpdateTenantRequestDTO;
import com.example.receipt_backend.dto.response.GenericResponseDTO;
import com.example.receipt_backend.dto.response.TenantResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.example.receipt_backend.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tenants")
@Tag(name = "Tenant Management", description = "APIs for managing tenants")
public class TenantController {

    @Autowired
    private TenantService tenantService;

    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN')")
    @PostMapping
    @Operation(summary = "Create a new tenant", description = "Allows a system admin to create a new tenant")
    public ResponseEntity<TenantResponseDTO> createTenant(@Validated @RequestBody TenantRequestDTO tenantRequest) {
        TenantResponseDTO createdTenant = tenantService.createTenant(tenantRequest);
        return new ResponseEntity<>(createdTenant, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ROLE_SYSTEM_ADMIN', 'ROLE_COMPANY_ADMIN')")
    @PutMapping("/{id}")
    @Operation(summary = "Update an existing tenant", description = "Allows a system or company admin to update tenant details")
    public ResponseEntity<TenantResponseDTO> updateTenant(
            @PathVariable UUID id,
            @Validated @RequestBody UpdateTenantRequestDTO tenantRequest) {
        TenantResponseDTO updatedTenant = tenantService.updateTenant(id, tenantRequest);
        return new ResponseEntity<>(updatedTenant, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a tenant", description = "Allows a system admin to delete a tenant")
    public ResponseEntity<GenericResponseDTO<String>> deleteTenant(@PathVariable UUID id) {
        tenantService.deleteTenant(id);
        return new ResponseEntity<>(new GenericResponseDTO<>("Tenant deleted successfully", "200"), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN')")
    @PutMapping("/{id}/inactivate")
    @Operation(summary = "Inactivate a tenant", description = "Allows a system admin to inactivate a tenant")
    public ResponseEntity<GenericResponseDTO<String>> inactivateTenant(@PathVariable UUID id) {
        tenantService.inactivateTenant(id);
        return new ResponseEntity<>(new GenericResponseDTO<>("Tenant inactivated successfully", "200"), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN')")
    @PutMapping("/{id}/activate")
    @Operation(summary = "Activate a tenant", description = "Allows a system admin to activate a tenant")
    public ResponseEntity<GenericResponseDTO<String>> activateTenant(@PathVariable UUID id) {
        tenantService.activateTenant(id);
        return new ResponseEntity<>(new GenericResponseDTO<>("Tenant activated successfully", "200"), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ROLE_SYSTEM_ADMIN', 'ROLE_COMPANY_ADMIN')")
    @GetMapping("/{id}")
    @Operation(summary = "Get tenant by ID", description = "Allows a system or company admin to retrieve tenant details by ID")
    public ResponseEntity<TenantResponseDTO> getTenantById(@PathVariable UUID id) {
        TenantResponseDTO tenant = tenantService.getTenantById(id);
        return new ResponseEntity<>(tenant, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN')")
    @GetMapping
    @Operation(summary = "Get all tenants", description = "Allows a system admin to retrieve all tenants")
    public ResponseEntity<List<TenantResponseDTO>> getAllTenants() {
        List<TenantResponseDTO> tenants = tenantService.getAllTenants();
        return new ResponseEntity<>(tenants, HttpStatus.OK);
    }
}
