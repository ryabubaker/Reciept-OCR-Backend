package com.example.receipt_backend.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileStorageService {


    String uploadFile(MultipartFile file, String tenantId, String requestId) throws IOException;

    void deleteFile(String fileUrl); // Delete file by URL

    String getFileUrl(String key);
}

