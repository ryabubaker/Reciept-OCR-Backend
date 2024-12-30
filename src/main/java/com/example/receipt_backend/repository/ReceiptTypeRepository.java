package com.example.receipt_backend.repository;

import com.example.receipt_backend.entity.ReceiptType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReceiptTypeRepository extends JpaRepository<ReceiptType, UUID> {

    Optional<ReceiptType> findByName(String receiptTypeName);
}

