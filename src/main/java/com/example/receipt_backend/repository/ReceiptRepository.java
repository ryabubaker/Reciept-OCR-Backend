package com.example.receipt_backend.repository;

import com.example.receipt_backend.entity.Receipt;
import com.example.receipt_backend.utils.ReceiptStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.UUID;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, UUID>, JpaSpecificationExecutor<Receipt> {


    Collection<Receipt> findAllByStatus(ReceiptStatus receiptStatus);
}
