package com.example.receipt_backend.entity;

import com.example.receipt_backend.entity.common.AbstractGenericPKAuditableEntity;
import com.example.receipt_backend.entity.common.AbstractGenericPrimaryKey;
import com.example.receipt_backend.utils.RoleType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "roles", schema = "public")
public class RoleEntity extends AbstractGenericPrimaryKey implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private RoleType name;

}
