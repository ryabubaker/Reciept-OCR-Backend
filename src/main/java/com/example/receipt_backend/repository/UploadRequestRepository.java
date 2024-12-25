package com.example.receipt_backend.repository;

import com.example.receipt_backend.entity.UploadRequest;
import com.example.receipt_backend.utils.RequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository

public interface UploadRequestRepository extends JpaRepository<UploadRequest, UUID> {
    Page<UploadRequest> findByStatus(RequestStatus requestStatus, Pageable pageable);
}
