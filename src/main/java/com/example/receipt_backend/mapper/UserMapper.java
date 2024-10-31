package com.example.receipt_backend.mapper;
import com.example.receipt_backend.dto.UserDTO;
import com.example.receipt_backend.entity.UserEntity;
import com.example.receipt_backend.mapper.RoleMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring", uses = RoleMapper.class)
public interface UserMapper  {

    @Mapping(target = "roles", source = "roles", qualifiedByName = "mapStringsToRoles")
    UserEntity toEntity(UserDTO dto);

    @Mapping(target = "roles", source = "roles", qualifiedByName = "mapRolesToStrings")
    UserDTO toDto(UserEntity entity);

    List<UserEntity> toEntityList(List<UserDTO> list);

    List<UserDTO> toDtoList(List<UserEntity> list);
}
