package com.example.receipt_backend.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String storeFile(MultipartFile file);

    void deleteFile(String fileUrl);
}
