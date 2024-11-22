package com.example.receipt_backend.dto.response;
import lombok.Data;
import java.time.LocalDateTime;


@Data
public class TenantResponseDTO {
    private Long id;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
