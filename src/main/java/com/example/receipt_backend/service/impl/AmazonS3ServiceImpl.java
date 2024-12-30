package com.example.receipt_backend.service.impl;

import com.example.receipt_backend.config.AwsProperties;
import com.example.receipt_backend.service.FileStorageService;
import com.example.receipt_backend.utils.AppUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.core.sync.RequestBody;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AmazonS3ServiceImpl implements FileStorageService {

    private final S3Client s3Client;
    private final AwsProperties awsProperties;
    @Override
    public String uploadFile(MultipartFile file, String tenantId, String requestId) throws IOException {
        String filename = AppUtils.generateRandomAlphaNumericString(10) + "_" + file.getOriginalFilename();
        String key = String.format("receipts/%s/%s/%s", tenantId, requestId, filename);
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(awsProperties.getS3().getBucket())
                            .key(key)
                            .acl(ObjectCannedACL.PRIVATE)
                            .build(),
                    RequestBody.fromBytes(file.getBytes())
            );

            return key;

    }

    @Override
    public void deleteFile(String key) {

        try {
            s3Client.deleteObject(
                    DeleteObjectRequest.builder()
                            .bucket(awsProperties.getS3().getBucket())
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
                        .bucket(awsProperties.getS3().getBucket())
                        .key(key)
                        .build()
        ).toExternalForm();
    }
}

