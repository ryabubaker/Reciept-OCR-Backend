package com.example.receipt_backend;

import com.example.receipt_backend.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAwareUserImpl")
@EnableConfigurationProperties(AppProperties.class)
public class ReceiptBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReceiptBackendApplication.class, args);
    }

}
