package com.example.receipt_backend.controller;

import com.example.receipt_backend.dto.ReceiptDTO;
import com.example.receipt_backend.dto.UpdateReceiptDto;
import com.example.receipt_backend.dto.response.GenericResponseDTO;
import com.example.receipt_backend.exception.ResourceNotFoundException;
import com.example.receipt_backend.service.ReceiptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/receipts")
@RequiredArgsConstructor
public class ReceiptController {

    private final ReceiptService receiptService;

    @GetMapping("/list")
    public ResponseEntity<List<ReceiptDTO>> listReceipts() {
        List<ReceiptDTO> receipts = receiptService.listReceipts();
        return ResponseEntity.ok(receipts);
    }

    @PatchMapping("/{receiptId}/approve")
    public ResponseEntity<ReceiptDTO> approveReceipt(
            @PathVariable UUID receiptId,
            @RequestBody Map<String, Object> updatedValues) {

        ReceiptDTO updatedReceipt = receiptService.approveReceipt(receiptId, updatedValues);

        return ResponseEntity.ok(updatedReceipt);
    }

    @GetMapping("/{receiptId}")
    public ResponseEntity<ReceiptDTO> getReceiptByReceiptId(@PathVariable UUID receiptId) {
        ReceiptDTO receiptDTO = receiptService.getReceiptDetails(receiptId);
        return ResponseEntity.ok(receiptDTO);
    }
    @PutMapping
    public ResponseEntity<GenericResponseDTO<Boolean>> updateReceipts(@RequestBody List<UpdateReceiptDto> dto){
        receiptService.updateReceiptsForApproval(dto);

        return new ResponseEntity<>(GenericResponseDTO.<Boolean>builder().response(true).messageCode("Updated successfully").build(), HttpStatus.OK);
    }

//    @PatchMapping("/{receiptId}/ocr")
//    public ResponseEntity<ReceiptDTO> updateOcrData(
//            @PathVariable UUID receiptId,
//            @RequestBody Map<String, String> updatedOcrData) {
//        ReceiptDTO updated = receiptService.updateOcrData(receiptId, updatedOcrData);
//        return ResponseEntity.ok(updated);
//    }


    @DeleteMapping("/{receiptId}")
    public ResponseEntity<GenericResponseDTO<Boolean>> deleteReceipt(@PathVariable UUID receiptId) {
        GenericResponseDTO<Boolean> dto = receiptService.deleteReceipt(receiptId);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @DeleteMapping("/bulk-delete")
    public ResponseEntity<GenericResponseDTO<Boolean>> deleteReceipts(@RequestBody List<UUID> receiptIds) {
        try {
            receiptService.deleteReceipts(receiptIds);
            return new ResponseEntity<>(GenericResponseDTO.<Boolean>builder().response(true).build(), HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            // If any of the receipts are not found
            return new ResponseEntity<>(GenericResponseDTO.<Boolean>builder().response(false).messageCode(e.getMessage()).build(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            // For any other errors
            return new ResponseEntity<>(GenericResponseDTO.<Boolean>builder().response(false).messageCode("Failed to delete receipts.").build(), HttpStatus.BAD_REQUEST);
        }
    }

}

