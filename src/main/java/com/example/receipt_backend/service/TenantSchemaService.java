package com.example.receipt_backend.service;

public interface TenantSchemaService {
    void createTenantSchema(String tenantId);

    void deleteTenantSchema(String tenantName);

    void validateTenantId(String tenantId);

    void renameTenantSchema(String oldSchemaName, String tenantName);
}
