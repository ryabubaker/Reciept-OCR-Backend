package com.example.receipt_backend.controller;

import com.example.receipt_backend.dto.request.UploadRequestDTO;
import com.example.receipt_backend.dto.response.GenericResponseDTO;
import com.example.receipt_backend.dto.response.UploadResponseDTO;
import com.example.receipt_backend.service.ReceiptService;
import com.example.receipt_backend.utils.RequestStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/request")
@RequiredArgsConstructor
@Tag(name = "Request Controller", description = "APIs for managing receipt requests")
public class RequestController {
    private final ReceiptService receiptService;

    @PostMapping("/upload")
    @PreAuthorize("hasRole('ROLE_MOBILE_USER')")
    @Operation(summary = "Upload Receipts", description = "Uploads receipts based on the provided request data")
    public ResponseEntity<GenericResponseDTO<Boolean>> uploadRequest(
            @Valid @ModelAttribute  @Parameter(description = "Data for uploading receipts") UploadRequestDTO requestDTO) {
        GenericResponseDTO<Boolean> dto= receiptService.uploadReceipts(requestDTO);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @GetMapping("/pending")
    @Operation(summary = "Get Pending Requests", description = "Retrieves a paginated list of pending receipt requests")
    public ResponseEntity<Page<UploadResponseDTO>> getPendingRequest(
            @Parameter(description = "Pagination information") Pageable pageable) {
        Page<UploadResponseDTO> pendingReceipts = receiptService.getPendingRequests(pageable);
        return ResponseEntity.ok(pendingReceipts);
    }


    @GetMapping("/{requestId}")
    @Operation(summary = "Get Receipts by Request ID", description = "Retrieves receipts for a specific request ID")
    public ResponseEntity<UploadResponseDTO> getReceiptsByRequestId(
            @Parameter(description = "Unique identifier of the request") @PathVariable UUID requestId) {
        UploadResponseDTO receipts = receiptService.getRequestById(requestId);
        return ResponseEntity.ok(receipts);
    }
@GetMapping("/after")
@Operation(summary = "Get Requests After Date", description = "Retrieves requests created after a specific date and time")
public ResponseEntity<List<UploadResponseDTO>> getRequestsAfterDate(
        @Parameter @RequestParam String dateTime) {
    LocalDateTime parsedDateTime = LocalDateTime.parse(dateTime);
    List<UploadResponseDTO> requests = receiptService.getRequestsAfterDate(parsedDateTime);
    return ResponseEntity.ok(requests);
}

    @PutMapping("/{requestId}")
    public ResponseEntity<GenericResponseDTO<String>> updateRequest(@PathVariable String requestId, @RequestParam String status) {
        receiptService.updateRequestStatus(requestId, status);

        return new ResponseEntity<>(GenericResponseDTO.<String>builder().response("Updated successfully").build(), HttpStatus.OK);
    }
}