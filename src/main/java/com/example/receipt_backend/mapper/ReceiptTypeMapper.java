package com.example.receipt_backend.mapper;

import com.example.receipt_backend.dto.request.ReceiptTypeRequestDTO;
import com.example.receipt_backend.dto.request.ReceiptTypeUpdateRequestDTO;
import com.example.receipt_backend.dto.response.ReceiptTypeResponseDTO;
import com.example.receipt_backend.entity.ReceiptType;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ReceiptTypeMapper {


    @Mapping(target = "receiptTypeId", ignore = true)
    @Mapping(target = "name", source = "name")
    @Mapping(target = "templatePath", ignore = true)
    @Mapping(target = "column2idxMap", ignore = true)
    ReceiptType toEntity(ReceiptTypeRequestDTO receiptTypeRequestDTO);

    @Mapping(target = "receiptTypeId", source = "receiptTypeId")
    @Mapping(target = "template", ignore = true)
    @Mapping(target = "column2idxMap", ignore = true)
    ReceiptTypeResponseDTO toResponseDTO(ReceiptType receiptType);



   
}