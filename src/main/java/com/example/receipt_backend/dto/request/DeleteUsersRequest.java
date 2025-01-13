package com.example.receipt_backend.dto.request;

import lombok.Data;

import java.util.List;
import java.util.UUID;


@Data
public class DeleteUsersRequest {

    private List<UUID> userIds;
}
