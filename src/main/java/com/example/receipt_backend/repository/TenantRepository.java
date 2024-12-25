package com.example.receipt_backend.repository;

import com.example.receipt_backend.entity.Tenant;
import com.fasterxml.jackson.databind.introspect.AnnotationCollector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, UUID> {
     Optional<Tenant> findByTenantId(UUID tenantId);
     boolean existsByTenantName(String tenantName);
    boolean existsByAdminUser_Email(String adminEmail);

    Tenant findByTenantName(String tenantName);
}
