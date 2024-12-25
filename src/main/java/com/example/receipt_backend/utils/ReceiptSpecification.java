package com.example.receipt_backend.utils;

import com.example.receipt_backend.entity.Receipt;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.UUID;

public class ReceiptSpecification {

    public static Specification<Receipt> hasStatus(String status) {
        return (root, query, cb) ->
                (status == null || status.isEmpty()) ? cb.conjunction() : cb.equal(root.get("status"), ReceiptStatus.valueOf(status.toUpperCase()));
    }

    public static Specification<Receipt> uploadedAfter(LocalDateTime fromDate) {
        return (root, query, cb) ->
                (fromDate == null) ? cb.conjunction() : cb.greaterThanOrEqualTo(root.get("uploadedAt"), fromDate);
    }

    public static Specification<Receipt> uploadedBefore(LocalDateTime toDate) {
        return (root, query, cb) ->
                (toDate == null) ? cb.conjunction() : cb.lessThanOrEqualTo(root.get("uploadedAt"), toDate);
    }

    public static Specification<Receipt> containsSearchQuery(String searchQuery) {
        return (root, query, cb) -> {
            if (searchQuery == null || searchQuery.isEmpty()) {
                return cb.conjunction();
            }
            String pattern = "%" + searchQuery.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("receiptId").as(String.class)), pattern),
                    cb.like(cb.lower(root.join("receiptType").get("name")), pattern),
                    cb.like(cb.lower(root.join("ocrData").get("dataValue")), pattern)
            );
        };
    }

    public static Specification<Receipt> belongsToTenant(UUID tenantId) {
        return (root, query, cb) -> cb.equal(root.get("tenant").get("tenantId"), tenantId);
    }
}
