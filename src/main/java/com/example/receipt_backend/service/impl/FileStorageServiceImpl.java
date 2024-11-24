package com.example.receipt_backend.service.impl;

import com.example.receipt_backend.service.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageServiceImpl implements FileStorageService {
    @Override
    public String storeFile(MultipartFile file) {
        // Save the file to a directory or cloud storage
        // Return the URL or file path
        return "file:///path/to/uploaded/file";
    }

    @Override
    public void deleteFile(String fileUrl) {
        // Delete the file from the storage
    }
}
