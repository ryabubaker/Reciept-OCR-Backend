package com.example.receipt_backend.mapper;

import com.example.receipt_backend.dto.request.TenantRequestDTO;
import com.example.receipt_backend.dto.response.TenantResponseDTO;
import com.example.receipt_backend.entity.Tenant;
import com.example.receipt_backend.mapper.common.GenericMapper;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TenantMapper extends GenericMapper<TenantRequestDTO, Tenant, TenantResponseDTO> {

    @Override
    Tenant toEntity(TenantRequestDTO dto);
    @Override
    TenantResponseDTO toDto(Tenant entity);

    @Override
    List<Tenant> toEntityList(List<TenantRequestDTO> dtos);

    @Override
    List<TenantResponseDTO> toDtoList(List<Tenant> entitys);


}
