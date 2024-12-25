// src/main/java/com/example/receipt_backend/dto/QueryDTO.java
package com.example.receipt_backend.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class QueryDTO {
    private UUID receiptId;
    private UUID requestId;
    private String status;
    private String searchQuery;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private Pageable pageable;
}
