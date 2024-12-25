package com.example.receipt_backend.mapper;

import com.example.receipt_backend.dto.request.UploadRequestDTO;
import com.example.receipt_backend.dto.response.UploadResponseDTO;
import com.example.receipt_backend.entity.UploadRequest;
import net.minidev.json.writer.ArraysMapper;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ReceiptMapper.class})
public interface UploadRequestMapper {

    @Mapping(target = "receipts", ignore = true)
    @Mapping(target="status", ignore = true)
    UploadRequest toEntity(UploadRequestDTO dto);


    @Mapping(target = "receipts", source = "receipts", qualifiedByName = "mapReceiptsToDTOs")
    @Mapping(target = "uploadedAt", source = "uploadedAt")
    UploadResponseDTO toResponseDTO(UploadRequest uploadRequest);

}
