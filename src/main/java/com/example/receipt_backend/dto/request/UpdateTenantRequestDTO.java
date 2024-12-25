package com.example.receipt_backend.dto.request;

import com.example.receipt_backend.utils.TenantStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UpdateTenantRequestDTO extends TenantRequestDTO {

    private TenantStatus status;
}
