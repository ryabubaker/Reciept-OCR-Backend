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
import java.io.Serializable;
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
            @Valid @ModelAttribute ReceiptTypeRequestDTO requestDTO) throws IOException {
        ReceiptTypeResponseDTO responseDTO = receiptTypeService.createReceiptType(requestDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/{name}")
    @PreAuthorize("hasAnyRole('ROLE_COMPANY_ADMIN', 'ROLE_MOBILE_USER', 'ROLE_DESKTOP_USER')")
    @Operation(summary = "Get receipt type by name", description = "Retrieve a specific receipt type by its Name")
    public ResponseEntity<ReceiptTypeResponseDTO> getReceiptTypeByName(
            @PathVariable String name) {
        ReceiptTypeResponseDTO responseDTO = receiptTypeService.getReceiptTypeByName(name);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_COMPANY_ADMIN', 'ROLE_MOBILE_USER', 'ROLE_DESKTOP_USER')")
    @Operation(summary = "Get all receipt types", description = "Retrieve a list of all receipt types")
    public ResponseEntity<List<Map<String, ? extends Serializable>>> getAllReceiptTypes() {
        List<Map<String, ? extends Serializable>> receiptTypes = receiptTypeService.getAllReceiptTypes();
        return ResponseEntity.ok(receiptTypes);
    }

    @PatchMapping("/{currentName}")
    @PreAuthorize("hasRole('ROLE_COMPANY_ADMIN')")
    @Operation(summary = "Update a receipt type", description = "Allows a company admin to update an existing receipt type")
    public ResponseEntity<ReceiptTypeResponseDTO> updateReceiptType(
            @PathVariable String currentName,
            @Valid @RequestBody ReceiptTypeUpdateRequestDTO requestDTO) throws IOException {
        ReceiptTypeResponseDTO receiptTypeResponseDTO = receiptTypeService.updateReceiptType(currentName, requestDTO);
        return ResponseEntity.ok(receiptTypeResponseDTO);
    }

    @DeleteMapping("/{name}")
    @PreAuthorize("hasRole('ROLE_COMPANY_ADMIN')")
    @Operation(summary = "Delete a receipt type", description = "Allows a company admin to delete a receipt type")
    public ResponseEntity<GenericResponseDTO<String>> deleteReceiptType(
            @PathVariable String name) throws IOException {
        receiptTypeService.deleteReceiptType(name);
        return ResponseEntity.ok(new GenericResponseDTO<>("Receipt type deleted successfully", "200"));
    }

}