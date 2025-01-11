package com.example.receipt_backend.service.impl;

import com.example.receipt_backend.security.JwtUtils;
import com.example.receipt_backend.service.InvitationService;
import com.example.receipt_backend.utils.AppUtils;
import com.example.receipt_backend.utils.RoleType;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InvitationServiceImpl implements InvitationService {
    private final JwtUtils jwtUtils;
    private Cache<String, InvitationDetails> invitationCache;

    @PostConstruct
    public void initCache() {
        invitationCache = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofHours(24)) // e.g., 24-hour TTL
                .maximumSize(10_000) // avoid unbounded growth
                .build();
    }
    @Override
    public String generateInvitationToken(String email, UUID tenantId, RoleType roleName) {

        String shortCode = AppUtils.generateRandomAlphaNumericString(8);
        InvitationDetails details = new InvitationDetails(email, tenantId, roleName);
        invitationCache.put(shortCode, details);
        return shortCode;
    }
    @Override
    public InvitationDetails validateInvitation(String shortCode) {
        // Attempt to retrieve from cache
        InvitationDetails details = invitationCache.getIfPresent(shortCode);
        if (details != null) {
            // Optionally mark code as used: invitationCache.invalidate(shortCode);
            return details;
        }
        return null;
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class InvitationDetails {
        private String email;
        private UUID tenantId;
        private RoleType role;
    }
}
