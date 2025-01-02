package com.example.receipt_backend.entity;

import com.example.receipt_backend.utils.ListMapToJsonConverter;
import com.example.receipt_backend.utils.MapToJsonConverter;
import com.example.receipt_backend.utils.ReceiptStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "receipt")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Receipt {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "receipt_id", updatable = false, nullable = false)
    private UUID receiptId;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private UploadRequest request;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receipt_type_id", nullable = false)
    private ReceiptType receiptType;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReceiptStatus status = ReceiptStatus.PENDING;

    @Convert(converter = ListMapToJsonConverter.class)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "ocr_data", columnDefinition = "jsonb")
    private List<Map<Integer, String>> ocrData;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by_user_id")
    private User approvedBy;

    @Column(name = "approved_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime approvedAt;

}
