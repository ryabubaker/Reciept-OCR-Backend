package com.example.receipt_backend.mapper;

import com.example.receipt_backend.dto.request.TenantRequestDTO;
import com.example.receipt_backend.dto.response.TenantResponseDTO;
import com.example.receipt_backend.entity.Tenant;
import com.example.receipt_backend.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface TenantMapper {

    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "adminUsers", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "users", ignore = true)
    Tenant toEntity(TenantRequestDTO dto);


    TenantResponseDTO toDto(Tenant entity);


    @Named("mapAdminUsersToIds")
    default List<UUID> mapAdminUsersToIds(List<User> adminUsers) {
        return adminUsers != null ? adminUsers.stream()
                .map(User::getId)
                .collect(Collectors.toList()) : Collections.emptyList();
    }
}