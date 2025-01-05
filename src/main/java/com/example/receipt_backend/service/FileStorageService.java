package com.example.receipt_backend.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

public interface FileStorageService {


    String uploadFile(MultipartFile file, String tenantId, String requestId) throws IOException;

    void uploadTemplate(String key, InputStream inputStream, long contentLength, String contentType);

    InputStream downloadFile(String key);

    void deleteFile(String fileUrl); // Delete file by URL

    String getFileUrl(String key);
}

