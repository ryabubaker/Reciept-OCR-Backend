package com.example.receipt_backend.mapper;

import com.example.receipt_backend.dto.UserDTO;
import com.example.receipt_backend.dto.request.RegisterUserRequestDTO;
import com.example.receipt_backend.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = RoleMapper.class)
public interface UserMapper {

    @Mapping(target = "role", source = "role", qualifiedByName = "mapStringToRole")
    User toEntity(UserDTO dto);

    @Mapping(target = "role", source = "role", qualifiedByName = "mapRoleToString")
    @Mapping(target = "tenantId", source = "tenant.tenantId")
    @Mapping(target = "tenantName",  source = "tenant.tenantName")
    UserDTO toDto(User entity);

    @Mapping(target = "registeredProviderName", constant = "local")
    UserDTO toUserDTO(RegisterUserRequestDTO request);

    List<User> toEntityList(List<UserDTO> list);

    List<UserDTO> toDtoList(List<User> list);

    @Mapping(target = "role", source = "role", qualifiedByName = "mapStringToRole")

    void updateEntity(UserDTO reqUserDTO, @MappingTarget User user);
}
