package com.example.receipt_backend.repository;

import com.example.receipt_backend.entity.Tenant;
import com.example.receipt_backend.utils.TenantStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, UUID> {
     Optional<Tenant> findByTenantId(UUID tenantId);
     boolean existsByTenantName(String tenantName);
    boolean existsByAdminUser_Email(String adminEmail);
    
    List<Tenant> findAllByStatus(TenantStatus status);

    Tenant findByTenantName(String tenantName);
}
