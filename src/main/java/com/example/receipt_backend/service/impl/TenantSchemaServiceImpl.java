package com.example.receipt_backend.service.impl;

import com.example.receipt_backend.exception.CustomAppException;
import com.example.receipt_backend.service.TenantSchemaService;
import org.flywaydb.core.Flyway;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.regex.Pattern;

@Service
public class TenantSchemaServiceImpl implements TenantSchemaService {

    private final DataSource dataSource;
    private static final Pattern VALID_SCHEMA_PATTERN = Pattern.compile("^[a-zA-Z0-9_]+$");

    public TenantSchemaServiceImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void createTenantSchema(String tenantId) {
        validateTenantId(tenantId);

        try (Connection connection = dataSource.getConnection()) {
            // Create schema
            String createSchemaQuery = "CREATE SCHEMA IF NOT EXISTS " + tenantId;
            connection.createStatement().executeUpdate(createSchemaQuery);

            // Run Flyway migrations for the new schema
            Flyway flyway = Flyway.configure()
                    .dataSource(dataSource)
                    .schemas(tenantId)
                    .load();
            flyway.migrate();
        } catch (SQLException e) {
            throw new CustomAppException("Failed to create schema for tenant: " + tenantId, e);
        }
    }

    @Override
    public void deleteTenantSchema(String tenantId) {
        validateTenantId(tenantId);
        try (Connection connection = dataSource.getConnection()) {
            connection.createStatement().executeUpdate("DROP SCHEMA IF EXISTS " + tenantId + " CASCADE");
            connection.createStatement().executeUpdate("DELETE FROM public.tenants WHERE tenant_id = '" + tenantId + "'");
        } catch (SQLException e) {
            throw new CustomAppException("Failed to delete schema for tenant: " + tenantId, e);
        }
    }

    @Override
    public void validateTenantId(String tenantId) {
        if (!VALID_SCHEMA_PATTERN.matcher(tenantId).matches()) {
            throw new IllegalArgumentException("Invalid tenant ID. Tenant IDs must be alphanumeric with underscores only.");
        }
    }


}
