package com.example.receipt_backend.entity;

import com.example.receipt_backend.entity.common.AbstractGenericPKAuditableEntity;
import com.example.receipt_backend.entity.common.AbstractGenericPrimaryKey;
import com.example.receipt_backend.utils.RoleType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter @Getter @AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "roles")
public class RoleEntity extends AbstractGenericPrimaryKey {


    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private RoleType name;

}
