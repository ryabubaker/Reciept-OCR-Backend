package com.example.receipt_backend.service.impl;

import com.example.receipt_backend.entity.RoleEntity;
import com.example.receipt_backend.exception.ErrorCode;
import com.example.receipt_backend.exception.ResourceNotFoundException;
import com.example.receipt_backend.repository.RoleRepository;
import com.example.receipt_backend.service.RoleService;
import com.example.receipt_backend.utils.RoleType;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "roles", key = "#roleType.name()")
    public RoleEntity getRoleByName(RoleType roleType) {
        return roleRepository.findByName(roleType)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND.getMessage()));
    }
}
