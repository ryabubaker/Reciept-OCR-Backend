package com.example.receipt_backend.service;

public interface TenantSchemaService {
    void createTenantSchema(String tenantId);

    void deleteTenantSchema(String tenantId);

    void validateTenantId(String tenantId);
}
