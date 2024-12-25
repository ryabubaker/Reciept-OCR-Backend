package com.example.receipt_backend.config.multitenant;

import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Component
public class MultiTenantConnectionProviderImpl implements MultiTenantConnectionProvider<String> {
    private static final Logger logger = LoggerFactory.getLogger(MultiTenantConnectionProviderImpl.class);

    private final DataSource datasource;

    public MultiTenantConnectionProviderImpl(DataSource dataSource) {
        this.datasource = dataSource;
    }

    @Override
    public Connection getAnyConnection() throws SQLException {
        return datasource.getConnection();
    }

    @Override
    public void releaseAnyConnection(Connection connection) throws SQLException {
        connection.close();
    }

    @Override
    public Connection getConnection(String tenantIdentifier) throws SQLException {
        logger.info("Get connection for  {}", tenantIdentifier);
        final Connection connection = getAnyConnection();
        executeSchemaSwitch(connection, tenantIdentifier);
        return connection;
    }

    @Override
    public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {
        logger.info("Release connection for tenant {}", tenantIdentifier);
        String DEFAULT_TENANT = "public";
        executeSchemaSwitch(connection, DEFAULT_TENANT);
        releaseAnyConnection(connection);
    }

    private void executeSchemaSwitch(Connection connection, String schemaName) throws SQLException {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.execute("SET SCHEMA '" + schemaName + "'");
            logger.info("Switched schema to {}", schemaName);
        } finally {
            if (statement != null) {
                statement.close();
                logger.info("");
            }
        }
    }

    @Override
    public boolean supportsAggressiveRelease() {
        return false;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public boolean isUnwrappableAs(Class aClass) {
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> aClass) {
        return null;
    }
}
