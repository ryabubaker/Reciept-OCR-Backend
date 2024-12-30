package com.example.receipt_backend.mapper;

import com.example.receipt_backend.dto.request.ReceiptTypeRequestDTO;
import com.example.receipt_backend.dto.request.ReceiptTypeUpdateRequestDTO;
import com.example.receipt_backend.dto.response.ReceiptTypeResponseDTO;
import com.example.receipt_backend.entity.ReceiptType;
import org.mapstruct.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static com.example.receipt_backend.utils.AppUtils.fromJson;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ReceiptTypeMapper {

    @Mapping(target = "name", source = "name")
    @Mapping(target = "templatePath", ignore = true)
    ReceiptType toEntity(ReceiptTypeRequestDTO receiptTypeRequestDTO);

    @Mapping(target = "template", source = "templatePath", qualifiedByName = "readTemplateFile")
    ReceiptTypeResponseDTO toResponseDTO(ReceiptType receiptType);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "name", source = "name")
    @Mapping(target = "templatePath", ignore = true)
    void updateEntity(ReceiptTypeUpdateRequestDTO dto, @MappingTarget ReceiptType entity);

    @Named("readTemplateFile")
    default Map<String, Object> readTemplateFile(String templatePath) throws IOException {
        String jsonContent = Files.readString(Path.of(templatePath));

        Map<String, Object> json;
        json = fromJson(jsonContent, Map.class);
        return json;
    }
}