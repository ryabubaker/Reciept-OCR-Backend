package com.example.receipt_backend.mapper;
import com.example.receipt_backend.dto.UserDTO;
import com.example.receipt_backend.dto.request.RegisterUserRequestDTO;
import com.example.receipt_backend.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = RoleMapper.class)
public interface UserMapper  {

    @Mapping(target = "roles", source = "roles", qualifiedByName = "mapStringsToRoles")
    User toEntity(UserDTO dto);

    @Mapping(target = "roles", source = "roles", qualifiedByName = "mapRolesToStrings")
    UserDTO toDto(User entity);

    @Mapping(target = "roles", source = "roles", qualifiedByName = "mapRolesToStrings")
    @Mapping(target = "registeredProviderName", constant = "SecurityEnums.AuthProviderId.app_custom_authentication")
    UserDTO toUserDTO(RegisterUserRequestDTO request);

    List<User> toEntityList(List<UserDTO> list);

    List<UserDTO> toDtoList(List<User> list);
}
