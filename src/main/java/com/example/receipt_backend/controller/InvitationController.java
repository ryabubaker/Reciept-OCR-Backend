package com.example.receipt_backend.controller;

import com.example.receipt_backend.dto.response.GenericResponseDTO;
import com.example.receipt_backend.mail.EmailService;
import com.example.receipt_backend.service.InvitationService;
import com.example.receipt_backend.utils.RoleType;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/invitations")
@RequiredArgsConstructor
public class InvitationController {

    private final InvitationService invitationService;
    private final EmailService emailService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN')")
    public ResponseEntity<GenericResponseDTO<String>> sendInvitation(@Valid @RequestBody InvitationRequest invitationRequest) {
            // Generate invitation token
            String token = invitationService.generateInvitationToken(
                    invitationRequest.getEmail(),
                    invitationRequest.getTenantId(),
                    RoleType.ROLE_COMPANY_ADMIN);

            // Send invitation email
            emailService.sendInvitationEmail(invitationRequest.getEmail(), token);

            GenericResponseDTO<String> response = new GenericResponseDTO<>("Invitation sent successfully." );
            return ResponseEntity.ok(response);

    }

    // DTO for invitation request
    @Getter
    @Setter
    public static class InvitationRequest {
        private String email;
        private UUID tenantId;
    }
}
