package com.example.receipt_backend.entity.common;


import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

import jakarta.persistence.*;

import java.io.Serializable;


@MappedSuperclass
@Getter
@Setter
public abstract class AbstractGenericPrimaryKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

}