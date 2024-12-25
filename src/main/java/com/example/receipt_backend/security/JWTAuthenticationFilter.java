package com.example.receipt_backend.security;

import com.example.receipt_backend.config.multitenant.CurrentTenantIdentifierResolverImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = parseJwt(request);
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                String email = jwtUtils.getUsernameFromJwt(jwt);

                CustomUserDetails userDetails = userDetailsService.loadUserByUsername(email);
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authentication);

                // Determine tenantName based on role
                boolean isSystemAdmin = userDetails.getAuthorities().stream()
                        .anyMatch(auth -> auth.getAuthority().equals("ROLE_SYSTEM_ADMIN"));

                String tenantName;
                if (isSystemAdmin) {
                    tenantName = "public";
                } else {
                    tenantName = jwtUtils.getTenantNameFromJwtToken(jwt);
                    if (tenantName == null || tenantName.isEmpty()) {
                        logger.error("Tenant ID is missing in JWT token for user: {}", email);
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Tenant ID is missing");
                        return;
                    }
                }

                CurrentTenantIdentifierResolverImpl.setTenant(tenantName);
                logger.debug("Tenant ID set to: {}", tenantName);

                // Mark the request as async-safe if needed
                request.setAttribute("isAsync", true); // Set to true for async tasks
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e.getMessage());
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            if (!isAsyncRequest(request)) {
                CurrentTenantIdentifierResolverImpl.clear();
                logger.debug("Tenant context cleared");
            }
        }
    }

    private boolean isAsyncRequest(HttpServletRequest request) {
        Object asyncFlag = request.getAttribute("isAsync");
        return asyncFlag != null && Boolean.TRUE.equals(asyncFlag);
    }



    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }

        return null;
    }
}
