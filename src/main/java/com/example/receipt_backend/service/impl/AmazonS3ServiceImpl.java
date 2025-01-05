package com.example.receipt_backend.service.impl;

import com.example.receipt_backend.service.FileStorageService;
import com.example.receipt_backend.utils.AppUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class AmazonS3ServiceImpl implements FileStorageService {
    private final S3Client s3Client;
    private final String bucketName;

    @Override
    public String uploadFile(MultipartFile file, String tenantId, String requestId) throws IOException {
        String filename = AppUtils.generateRandomAlphaNumericString(10) + "_" + file.getOriginalFilename();
        String key = String.format("receipts/%s/%s/%s", tenantId, requestId, filename);
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .acl(ObjectCannedACL.PRIVATE)
                            .build(),
                    RequestBody.fromBytes(file.getBytes())
            );

            return key;

    }
    @Override
    public void uploadTemplate(String key, InputStream inputStream, long contentLength, String contentType) {
        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .contentType(contentType)
                        .contentLength(contentLength)
                        .build(),
                RequestBody.fromInputStream(inputStream, contentLength)
        );

    }
    @Override
    public InputStream downloadFile(String key) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        return s3Client.getObject(getObjectRequest);
    }


    @Override
    public void deleteFile(String key) {

        try {
            s3Client.deleteObject(
                    DeleteObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete file from S3", e);
        }
    }

    @Override
    public String getFileUrl(String key) {
        return s3Client.utilities().getUrl(
                GetUrlRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .build()
        ).toExternalForm();
    }
}

