package com.example.receipt_backend.config;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Slf4j
@Component
@ConfigurationProperties(prefix = "aws")
public class AwsProperties {

    public AwsProperties() {
        log.info("AWS Properties Initialized");
    }

    private String access;
    private String secret;

    private S3 s3 = new S3();

    @Getter
    @Setter
    public static class S3 {
        private String bucket;
        private String region;
    }
}
