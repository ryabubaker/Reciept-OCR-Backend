package com.example.receipt_backend.dto.response;
import com.example.receipt_backend.utils.TenantStatus;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;


@Data
public class TenantResponseDTO {
    private UUID tenantId;
    private String tenantName;
    private UUID adminUserId;
    private String adminEmail;
    private TenantStatus status;
}
