package com.example.receipt_backend.config;


import com.example.receipt_backend.utils.AppUtils;
import com.example.receipt_backend.utils.MapToJsonConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class AppConfig {

    private final ObjectMapper objectMapper;

    public AppConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    @Bean
    public AppUtils appUtils() {
        return new AppUtils(objectMapper);
    }




}
