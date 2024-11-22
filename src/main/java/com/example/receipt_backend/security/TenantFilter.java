package com.example.receipt_backend.security;

import com.example.receipt_backend.config.multitenant.CurrentTenantIdentifierResolverImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class TenantFilter extends OncePerRequestFilter {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String tenantId = request.getHeader("X-Tenant-ID");

        // Validate tenant
        if (tenantId == null || !isTenantValid(tenantId)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Tenant ID");
            return;
        }

        // Set tenant context
        CurrentTenantIdentifierResolverImpl.setTenant(tenantId);
        try {
            filterChain.doFilter(request, response);
        } finally {
            CurrentTenantIdentifierResolverImpl.clear();
        }
    }

    private boolean isTenantValid(String tenantId) {
        String query = "SELECT COUNT(*) FROM public.tenants WHERE tenant_id = ?";
        Integer count = jdbcTemplate.queryForObject(query, new Object[]{tenantId}, Integer.class);
        return count != null && count > 0;
    }
}
