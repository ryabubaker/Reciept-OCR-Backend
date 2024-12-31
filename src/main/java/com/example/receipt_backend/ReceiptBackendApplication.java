package com.example.receipt_backend;

import com.example.receipt_backend.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAwareUserImpl")
@EnableConfigurationProperties(AppProperties.class)
@EnableRetry
@OpenAPIDefinition(info = @Info(title = "Receipt API", version = "1.0", description = "Documentation APIs v1.0"))
public class ReceiptBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReceiptBackendApplication.class, args);
    }

}