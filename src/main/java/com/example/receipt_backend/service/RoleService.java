package com.example.receipt_backend.service;

import com.example.receipt_backend.entity.RoleEntity;
import com.example.receipt_backend.utils.RoleType;

public interface RoleService {
    RoleEntity getRoleByName(RoleType name);
}
