package com.example.receipt_backend.config;


import com.example.receipt_backend.utils.AppUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    private final AppProperties appProperties;
    private final ObjectMapper objectMapper;

    public AppConfig(AppProperties appProperties,
                     ObjectMapper objectMapper) {
        this.appProperties = appProperties;
        this.objectMapper = objectMapper;
    }

    @Bean
    public AppUtils appUtils() {
        return new AppUtils(objectMapper);
    }



}
