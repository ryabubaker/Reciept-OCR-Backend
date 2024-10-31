package com.example.receipt_backend.mapper;

import com.example.receipt_backend.entity.RoleEntity;
import com.example.receipt_backend.utils.RoleType;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    @Named("mapRoleToString")
    default String mapRoleToString(RoleEntity role) {
        return role.getName().toString();
    }

    @Named("mapStringToRole")
    default RoleEntity mapStringToRole(String roleName) {
        RoleEntity role = new RoleEntity();
        role.setName(RoleType.valueOf(roleName));
        return role;
    }

    @Named("mapRolesToStrings")
    default Set<String> mapRolesToStrings(Set<RoleEntity> roles) {
        return roles.stream()
                .map(this::mapRoleToString)
                .collect(Collectors.toSet());
    }

    @Named("mapStringsToRoles")
    default Set<RoleEntity> mapStringsToRoles(Set<String> roleNames) {
        return roleNames.stream()
                .map(this::mapStringToRole)
                .collect(Collectors.toSet());
    }
}

