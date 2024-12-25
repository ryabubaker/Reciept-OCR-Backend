package com.example.receipt_backend.mapper;
import com.example.receipt_backend.entity.RoleEntity;
import com.example.receipt_backend.exception.ResourceNotFoundException;
import com.example.receipt_backend.repository.RoleRepository;
import com.example.receipt_backend.utils.RoleType;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;
@Mapper(componentModel = "spring")
public abstract class RoleMapper {
    @Autowired
    private RoleRepository roleRepository;

    @Named("mapRoleToString")
    String mapRoleToString(RoleEntity role) {
        return role.getName().toString();
    }
    @Named("mapStringToRole")
    RoleEntity mapStringToRole(String roleName) {
        return roleRepository.findByName(RoleType.valueOf(roleName))
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleName));
    }

    @Named("mapRolesToStrings")
    Set<String> mapRolesToStrings(Set<RoleEntity> roles) {
        return roles.stream()
                .map(this::mapRoleToString)
                .collect(Collectors.toSet());
    }
    @Named("mapStringsToRoles")
    Set<RoleEntity> mapStringsToRoles(Set<String> roleNames) {
        return roleNames.stream()
                .map(this::mapStringToRole)
                .collect(Collectors.toSet());
    }
}