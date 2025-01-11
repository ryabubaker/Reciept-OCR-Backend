package com.example.receipt_backend.service;

import com.example.receipt_backend.service.impl.InvitationServiceImpl;
import com.example.receipt_backend.utils.RoleType;

import java.util.UUID;

public interface InvitationService {
    String generateInvitationToken(String email, UUID tenantId, RoleType roleName);

    InvitationServiceImpl.InvitationDetails validateInvitation(String shortCode);
}
