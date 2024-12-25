package com.example.receipt_backend.controller;

import com.example.receipt_backend.dto.request.ReceiptTypeRequestDTO;
import com.example.receipt_backend.dto.request.ReceiptTypeUpdateRequestDTO;
import com.example.receipt_backend.dto.response.GenericResponseDTO;
import com.example.receipt_backend.dto.response.ReceiptTypeResponseDTO;
import com.example.receipt_backend.service.ReceiptTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/receipt-types")
@RequiredArgsConstructor
@Tag(name = "Receipt Type Management", description = "APIs for managing receipt types")
public class ReceiptTypeController {

    private final ReceiptTypeService receiptTypeService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_COMPANY_ADMIN')")
    @Operation(summary = "Create a new receipt type", description = "Allows a company admin to create a new receipt type")
    public ResponseEntity<ReceiptTypeResponseDTO> createReceiptType(
            @Valid @RequestBody ReceiptTypeRequestDTO requestDTO) {
        ReceiptTypeResponseDTO responseDTO = receiptTypeService.createReceiptType(requestDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @GetMapping("/{receiptTypeId}")
    @PreAuthorize("hasAnyRole('ROLE_COMPANY_ADMIN', 'ROLE_MOBILE_USER', 'ROLE_DESKTOP_USER')")
    @Operation(summary = "Get receipt type by ID", description = "Retrieve a specific receipt type by its ID")
    public ResponseEntity<ReceiptTypeResponseDTO> getReceiptTypeById(
            @PathVariable UUID receiptTypeId) {
        ReceiptTypeResponseDTO responseDTO = receiptTypeService.getReceiptTypeById(receiptTypeId);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_COMPANY_ADMIN', 'ROLE_MOBILE_USER', 'ROLE_DESKTOP_USER')")
    @Operation(summary = "Get all receipt types", description = "Retrieve a list of all receipt types")
    public ResponseEntity<List<ReceiptTypeResponseDTO>> getAllReceiptTypes() {
        List<ReceiptTypeResponseDTO> receiptTypes = receiptTypeService.getAllReceiptTypes();
        return ResponseEntity.ok(receiptTypes);
    }

    @PatchMapping("/{receiptTypeId}")
    @PreAuthorize("hasRole('ROLE_COMPANY_ADMIN')")
    @Operation(summary = "Update a receipt type", description = "Allows a company admin to update an existing receipt type")
    public ResponseEntity<ReceiptTypeResponseDTO> updateReceiptType(
            @PathVariable UUID receiptTypeId,
            @Valid @RequestBody ReceiptTypeUpdateRequestDTO requestDTO) {
        ReceiptTypeResponseDTO receiptTypeResponseDTO = receiptTypeService.updateReceiptType(receiptTypeId, requestDTO);
        return ResponseEntity.ok(receiptTypeResponseDTO);
    }

    @DeleteMapping("/{receiptTypeId}")
    @PreAuthorize("hasRole('ROLE_COMPANY_ADMIN')")
    @Operation(summary = "Delete a receipt type", description = "Allows a company admin to delete a receipt type")
    public ResponseEntity<GenericResponseDTO<String>> deleteReceiptType(
            @PathVariable UUID receiptTypeId) {
        receiptTypeService.deleteReceiptType(receiptTypeId);
        return ResponseEntity.ok(new GenericResponseDTO<>("Receipt type deleted successfully", "200"));
    }

    @DeleteMapping("/{receiptTypeId}/fields/{fieldName}")
    public ResponseEntity<GenericResponseDTO<String>> deleteReceiptField(
            @PathVariable UUID receiptTypeId,
            @PathVariable String fieldName) {

        receiptTypeService.deleteReceiptField(receiptTypeId, fieldName);
        return ResponseEntity.ok(new GenericResponseDTO<>("Field { } deleted successfully" + fieldName, "200"));
    }
}