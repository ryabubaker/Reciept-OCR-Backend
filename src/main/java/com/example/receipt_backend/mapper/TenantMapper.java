package com.example.receipt_backend.mapper;

import com.example.receipt_backend.dto.request.TenantRequestDTO;
import com.example.receipt_backend.dto.response.TenantResponseDTO;
import com.example.receipt_backend.entity.Tenant;
import com.example.receipt_backend.mapper.common.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TenantMapper extends GenericMapper<TenantRequestDTO, Tenant, TenantResponseDTO> {

    @Override
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "adminUser", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    Tenant toEntity(TenantRequestDTO dto);

    @Override
    @Mapping(target = "adminUserId", source = "adminUser.id")
    @Mapping(target = "adminEmail", source = "adminUser.email")
    TenantResponseDTO toDto(Tenant entity);

    @Override
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "adminUser", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    List<Tenant> toEntityList(List<TenantRequestDTO> dtos);

    @Override
    List<TenantResponseDTO> toDtoList(List<Tenant> entitys);

    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "adminUser", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    void updateEntity(TenantRequestDTO dto, @MappingTarget Tenant entity);
}