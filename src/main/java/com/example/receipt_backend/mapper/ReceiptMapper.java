package com.example.receipt_backend.mapper;

import com.example.receipt_backend.dto.ReceiptDTO;
import com.example.receipt_backend.entity.Receipt;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {TenantMapper.class})
public interface ReceiptMapper {

    @Mapping(source = "receiptType.name", target = "receiptFormatName") // Map ReceiptType name
    @Mapping(source = "tenant.tenantId", target = "tenantId") // Map Tenant ID through the Tenant relationship
    ReceiptDTO toDTO(Receipt receipt);
}


