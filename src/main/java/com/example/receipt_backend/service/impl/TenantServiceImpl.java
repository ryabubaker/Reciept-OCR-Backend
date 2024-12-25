package com.example.receipt_backend.service.impl;

import com.example.receipt_backend.dto.UserDTO;
import com.example.receipt_backend.dto.request.TenantRequestDTO;
import com.example.receipt_backend.dto.request.UpdateTenantRequestDTO;
import com.example.receipt_backend.dto.response.TenantResponseDTO;
import com.example.receipt_backend.entity.RoleEntity;
import com.example.receipt_backend.entity.Tenant;
import com.example.receipt_backend.entity.User;
import com.example.receipt_backend.exception.AppExceptionConstants;
import com.example.receipt_backend.exception.CustomAppException;
import com.example.receipt_backend.exception.ResourceNotFoundException;
import com.example.receipt_backend.mail.EmailService;
import com.example.receipt_backend.mapper.TenantMapper;
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
    private final UserRepository userRepository;
    private final UserService userService;
    private final RoleService roleService;
    private final EmailService emailService;

    @Override
    @Transactional
    public TenantResponseDTO createTenant(TenantRequestDTO request) {
        // Check if tenant name already exists
        if (tenantRepository.existsByTenantName(request.getTenantName())) {
            throw new IllegalArgumentException("Tenant name already exists.");
        }
        tenantSchemaService.createTenantSchema(request.getTenantName());
        
        // Convert the request to a Tenant entity
        Tenant tenant = tenantMapper.toEntity(request);
        
        // Save the tenant to generate a tenantId
        Tenant savedTenant = tenantRepository.save(tenant);
    
        // Create the admin user with the saved tenant
        User adminUser = createAdminUser(request, savedTenant);
    
        // Set the admin user for the tenant
        savedTenant.setAdminUser(adminUser);
        
        // Save the tenant again with the admin user set
        savedTenant = tenantRepository.save(savedTenant);
        
        return tenantMapper.toDto(savedTenant);
    }
    
    @Transactional
    public User createAdminUser(TenantRequestDTO request, Tenant tenant) {
        // Check if the admin email is already associated with another tenant
        if (userRepository.existsByEmailAndTenant_TenantName(request.getAdminEmail(), request.getTenantName())) {
            throw new IllegalArgumentException("Admin email already associated with another tenant.");
        }
    
        // Fetch the ROLE_COMPANY_ADMIN role
        RoleEntity companyAdminRole = roleService.getRoleByName(RoleType.ROLE_COMPANY_ADMIN);
    
        // Generate a random password for the admin user
        String generatedPassword = AppUtils.generateRandomAlphaNumericString(10);
    
        // Build UserDTO with the correct tenantId
        UserDTO adminUserDto = UserDTO.builder()
                .username(request.getAdminEmail())
                .email(request.getAdminEmail())
                .emailVerified(true)
                .password(generatedPassword)
                .registeredProviderName(SecurityEnums.AuthProviderId.local)
                .roles(Set.of(companyAdminRole.getName().toString()))
                .tenantId(tenant.getTenantId().toString()) // Use tenantId from the saved tenant
                .build();
    
        // Create the admin user
        User createdAdminUser = userService.createUser(adminUserDto, tenant.getTenantId().toString(), RoleType.ROLE_COMPANY_ADMIN);
    
        // Send a welcome email with the generated password
        emailService.sendWelcomeEmailWithPassword(createdAdminUser.getEmail(), createdAdminUser.getEmail(), generatedPassword);
    
        // Retrieve the admin user from the repository
        User adminUser = userRepository.findByEmail(createdAdminUser.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException(AppExceptionConstants.USER_RECORD_NOT_FOUND));
    
        return adminUser;
    }


    @Override
    @Transactional
    public TenantResponseDTO updateTenant(UUID tenantId, UpdateTenantRequestDTO request) {
        Tenant tenant = getTenant(tenantId);
        
        if (request.getTenantName() != null && !tenant.getTenantName().equals(request.getTenantName())) {
            if (tenantRepository.existsByTenantName(request.getTenantName())) {
                throw new IllegalArgumentException("Tenant name already exists.");
            }
            String oldSchemaName = tenant.getTenantName();
            tenant.setTenantName(request.getTenantName());
            tenantSchemaService.renameTenantSchema(oldSchemaName, request.getTenantName());
        }

        if (request.getAdminEmail() != null && !tenant.getAdminUser().getEmail().equals(request.getAdminEmail())) {

            User newAdmin = createAdminUser(request, tenant);

            // Demote Old Admin
            User oldAdmin = tenant.getAdminUser();
            oldAdmin.getRoles().removeIf(role -> role.getName() == RoleType.ROLE_COMPANY_ADMIN);
            userRepository.save(oldAdmin);

            // Update Tenant Admin
            tenant.setAdminUser(newAdmin);
        }

        if (request.getStatus() != null) {
            tenant.setStatus(request.getStatus());
        }

        Tenant updatedTenant = tenantRepository.save(tenant);

        return tenantMapper.toDto(updatedTenant);
    }

    @Override
    @Transactional
    public void inactivateTenant(UUID tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException(AppExceptionConstants.TENANT_NOT_FOUND));

        tenant.setStatus(TenantStatus.INACTIVE);
        tenantRepository.save(tenant);
    }

    @Override
    @Transactional
    public void activateTenant(UUID tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException(AppExceptionConstants.TENANT_NOT_FOUND));

        tenant.setStatus(TenantStatus.ACTIVE);
        tenantRepository.save(tenant);
    }

    @Override
    @Transactional
    public void deleteTenant(UUID tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException(AppExceptionConstants.TENANT_NOT_FOUND));
        try {
            tenantSchemaService.deleteTenantSchema(tenant.getTenantName());
        } catch (CustomAppException e) {
            throw new CustomAppException("Failed to delete tenant schema. Tenant deletion aborted.", e);
        }        tenantRepository.delete(tenant);
    }

    @Override
    @Transactional(readOnly = true)
    public TenantResponseDTO getTenantById(UUID id) {
        Tenant tenant = getTenant(id);
        return tenantMapper.toDto(tenant);
    }

    private Tenant getTenant(UUID id) {
        return tenantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(AppExceptionConstants.TENANT_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TenantResponseDTO> getAllTenants() {
        return tenantRepository.findAll().stream()
                .map(tenantMapper::toDto)
                .collect(Collectors.toList());
    }
}
