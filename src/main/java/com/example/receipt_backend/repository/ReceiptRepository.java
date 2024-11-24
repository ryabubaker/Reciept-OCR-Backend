package com.example.receipt_backend.repository;

import com.example.receipt_backend.entity.Receipt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Long> {

    Page<Receipt> findByTenantIdAndUserId(Long tenantId, Long userId, Pageable pageable);
    Page<Receipt> findByUserId(Long userId, Pageable pageable);
}
