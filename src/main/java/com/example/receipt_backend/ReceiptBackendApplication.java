package com.example.receipt_backend;

import com.example.receipt_backend.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAwareUserImpl")
@EnableConfigurationProperties(AppProperties.class)
@EnableRetry
public class ReceiptBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReceiptBackendApplication.class, args);
    }

}
