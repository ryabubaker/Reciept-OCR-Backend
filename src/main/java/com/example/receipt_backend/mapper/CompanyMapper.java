package com.example.receipt_backend.mapper;

import com.example.receipt_backend.dto.request.CompanyRequestDTO;
import com.example.receipt_backend.dto.response.CompanyResponseDTO;
import com.example.receipt_backend.entity.Tenant;
import com.example.receipt_backend.mapper.common.GenericMapper;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CompanyMapper extends GenericMapper<CompanyRequestDTO, Tenant, CompanyResponseDTO> {

    @Override
    Tenant toEntity(CompanyRequestDTO dto);
    @Override
    CompanyResponseDTO toDto(Tenant entity);

    @Override
    List<Tenant> toEntityList(List<CompanyRequestDTO> dtos);

    @Override
    List<CompanyResponseDTO> toDtoList(List<Tenant> entitys);


}
