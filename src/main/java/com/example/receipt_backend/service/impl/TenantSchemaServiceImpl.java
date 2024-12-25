package com.example.receipt_backend.service.impl;

import com.example.receipt_backend.exception.CustomAppException;
import com.example.receipt_backend.service.TenantSchemaService;
import org.flywaydb.core.Flyway;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Pattern;

@Service
public class TenantSchemaServiceImpl implements TenantSchemaService {

    private final DataSource dataSource;
    private static final Pattern VALID_SCHEMA_PATTERN = Pattern.compile("^[a-zA-Z0-9_]+$");

    @Value("${myapp.tenant-migration-location}")
    private String flywayLocations;

    public TenantSchemaServiceImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void createTenantSchema(String tenant) {
        validateTenantId(tenant);

        try (Connection connection = dataSource.getConnection();
             Statement stmt = connection.createStatement()) {

            // Quote schema name to avoid SQL injection
            String quotedSchema = "\"" + tenant + "\"";

            // Create schema
            String createSchemaQuery = "CREATE SCHEMA IF NOT EXISTS " + quotedSchema;
            stmt.executeUpdate(createSchemaQuery);

            // Configure Flyway for the new tenant schema
            Flyway flyway = Flyway.configure()
                    .dataSource(dataSource)
                    .schemas(tenant)
                    .locations(flywayLocations)
                    .load();

            // Migrate tenant schema
            flyway.migrate();

        } catch (SQLException e) {
            throw new CustomAppException("Failed to create schema for tenant: " + tenant, e);
        }
    }

    public void deleteTenantSchema(String tenantName) {
        try (Connection connection = dataSource.getConnection();
             Statement stmt = connection.createStatement()) {

            // Validate schema existence
            String schemaCheckQuery = "SELECT schema_name FROM information_schema.schemata WHERE schema_name = '" + tenantName + "'";
            ResultSet rs = stmt.executeQuery(schemaCheckQuery);

            if (!rs.next()) {
                throw new CustomAppException("Schema " + tenantName + " does not exist.");
            }

            // Drop schema
            String dropSchemaQuery = "DROP SCHEMA IF EXISTS \"" + tenantName + "\" CASCADE";
            stmt.executeUpdate(dropSchemaQuery);

        } catch (SQLException e) {
            throw new CustomAppException("Failed to delete schema for tenant: " + tenantName, e);
        }
    }

    public void renameTenantSchema(String oldSchemaName, String newSchemaName) {
        validateTenantId(newSchemaName);
        try (Connection connection = dataSource.getConnection();
             Statement stmt = connection.createStatement()) {

            String renameSchemaQuery = String.format("ALTER SCHEMA \"%s\" RENAME TO \"%s\"", oldSchemaName, newSchemaName);
            stmt.executeUpdate(renameSchemaQuery);

        } catch (SQLException e) {
            throw new CustomAppException("Failed to rename schema from " + oldSchemaName + " to " + newSchemaName, e);
        }
    }


    @Override
    public void validateTenantId(String tenant) {
        if (!VALID_SCHEMA_PATTERN.matcher(tenant).matches()) {
            throw new IllegalArgumentException("Invalid tenant name. Must be alphanumeric with underscores only.");
        }
    }
}
