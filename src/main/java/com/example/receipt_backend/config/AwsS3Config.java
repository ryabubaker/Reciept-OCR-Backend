package com.example.receipt_backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.regions.Region;

/**
 * This class handles the basic configurations needed to connect the app to amazon s3 service.
 */
@Configuration
public class AwsS3Config {
    // == fields ==
    @Value("${aws.access}")
    private String accessKey;
    @Value("${aws.secret}")
    private String secretAccessKey;
    @Value("${aws.s3.region}")
    private String region;
    @Value("${aws.s3.bucket}")
    private String bucketName;

    // == beans methods ==
    @Bean
    public S3Client s3Client() {
        AwsCredentials credentials = AwsBasicCredentials.create(accessKey, secretAccessKey);
        Region region = Region.of(this.region);

        return S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .region(region).build();
    }

    @Bean
    @S3BucketName
    public String s3BucketName() {
        return this.bucketName;
    }
}