package com.example.receipt_backend.dto.request;



import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Data
public class UploadRequestDTO {
    private MultipartFile[] files;
    private String receiptTypeId;
}

