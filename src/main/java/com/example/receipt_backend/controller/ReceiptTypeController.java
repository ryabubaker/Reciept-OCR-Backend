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

import java.io.IOException;
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
            @Valid @RequestBody ReceiptTypeRequestDTO requestDTO) throws IOException {
        ReceiptTypeResponseDTO responseDTO = receiptTypeService.createReceiptType(requestDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_COMPANY_ADMIN', 'ROLE_MOBILE_USER', 'ROLE_DESKTOP_USER')")
    @Operation(summary = "Get receipt type by name", description = "Retrieve a specific receipt type by its Name")
    public ResponseEntity<ReceiptTypeResponseDTO> getReceiptTypeById(
            @PathVariable String id) {
        ReceiptTypeResponseDTO responseDTO = receiptTypeService.getReceiptTypeById(id);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_COMPANY_ADMIN', 'ROLE_MOBILE_USER', 'ROLE_DESKTOP_USER')")
    @Operation(summary = "Get all receipt types", description = "Retrieve a list of all receipt types")
    public ResponseEntity<List<Map<String, Object>>> getAllReceiptTypes() {
        List<Map<String, Object>> receiptTypes = receiptTypeService.getAllReceiptTypes();
        return ResponseEntity.ok(receiptTypes);
    }

    @GetMapping("/json")
    @PreAuthorize("hasAnyRole('ROLE_COMPANY_ADMIN', 'ROLE_MOBILE_USER', 'ROLE_DESKTOP_USER')")
    @Operation(summary = "Get all receipt types with Json", description = "Retrieve a list of all receipt types with their Json")
    public ResponseEntity<List<ReceiptTypeResponseDTO>> getAllReceiptTypesWithJson() {
        List<ReceiptTypeResponseDTO> receiptTypes = receiptTypeService.getAllReceiptTypesWithJson();
        return ResponseEntity.ok(receiptTypes);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_COMPANY_ADMIN')")
    @Operation(summary = "Update a receipt type", description = "Allows a company admin to update an existing receipt type")
    public ResponseEntity<ReceiptTypeResponseDTO> updateReceiptType(
            @PathVariable String id,
            @Valid @RequestBody ReceiptTypeUpdateRequestDTO requestDTO) throws IOException {
        ReceiptTypeResponseDTO receiptTypeResponseDTO = receiptTypeService.updateReceiptType(id, requestDTO);
        return ResponseEntity.ok(receiptTypeResponseDTO);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_COMPANY_ADMIN')")
    @Operation(summary = "Delete a receipt type", description = "Allows a company admin to delete a receipt type")
    public ResponseEntity<GenericResponseDTO<Boolean>> deleteReceiptType(
            @PathVariable String id) throws IOException {
        GenericResponseDTO<Boolean> dto= receiptTypeService.deleteReceiptType(id);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

}