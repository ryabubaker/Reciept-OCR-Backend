package com.example.receipt_backend.entity;


import com.example.receipt_backend.entity.common.AbstractGenericPKAuditableEntity;
import com.example.receipt_backend.utils.RequestStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Table(name = "upload_requests")
@EntityListeners(AuditingEntityListener.class)
public class UploadRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "request_id", nullable = false)
    private UUID requestId;

    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Receipt> receipts;


    @Enumerated(EnumType.STRING)
    private RequestStatus status = RequestStatus.PENDING;

    @CreatedBy
    @ManyToOne
    @JoinColumn(name = "uploaded_by_user_id", nullable = false)
    private User uploadedBy;

    @CreatedDate
    @Column(name = "uploaded_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime uploadedAt;

}
