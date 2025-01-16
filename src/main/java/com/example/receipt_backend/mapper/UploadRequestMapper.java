package com.example.receipt_backend.mapper;

import com.example.receipt_backend.dto.request.UploadRequestDTO;
import com.example.receipt_backend.dto.response.UploadResponseDTO;
import com.example.receipt_backend.entity.UploadRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ReceiptMapper.class})
public interface UploadRequestMapper {

    @Mapping(target = "receipts", ignore = true)
    @Mapping(target="status", ignore = true)
    @Mapping(target = "requestId", ignore = true)
    @Mapping(target = "uploadedBy", ignore = true)
    @Mapping(target = "uploadedAt", ignore = true)
    UploadRequest toEntity(UploadRequestDTO dto);


    @Mapping(target = "receipts", source = "receipts", qualifiedByName = "mapReceiptsToDTOs")
    @Mapping(target = "uploadedAt", source = "uploadedAt")
    @Mapping(target = "uploadedBy", source = "uploadedBy.id")
    @Mapping(target = "status", source = "status")
    UploadResponseDTO toResponseDTO(UploadRequest uploadRequest);

}
