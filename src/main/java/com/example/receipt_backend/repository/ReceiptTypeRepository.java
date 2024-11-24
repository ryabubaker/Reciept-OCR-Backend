package com.example.receipt_backend.repository;

import com.example.receipt_backend.entity.ReceiptType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReceiptTypeRepository extends JpaRepository<ReceiptType, Long> {
}

