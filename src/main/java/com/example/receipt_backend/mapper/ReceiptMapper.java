package com.example.receipt_backend.mapper;

import com.example.receipt_backend.dto.ReceiptDTO;
import com.example.receipt_backend.entity.Receipt;
import com.example.receipt_backend.entity.UploadRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ReceiptMapper {

    @Named("mapReceiptsToDTOs")
    List<ReceiptDTO> mapReceiptsToDTOs(List<Receipt> receipts);

    @Mapping(target = "receiptTypeId", source = "receiptType.receiptTypeId")
    @Mapping(target = "approvedBy", source = "approvedBy.id")
    ReceiptDTO
    toDTO(Receipt receipt);

    @Mapping(target = "receiptType.receiptTypeId", source = "receiptTypeId")
    @Mapping(target = "request", source = "uploadRequest")
    Receipt toEntity(String imageUrl, UploadRequest uploadRequest, UUID receiptTypeId);
}
