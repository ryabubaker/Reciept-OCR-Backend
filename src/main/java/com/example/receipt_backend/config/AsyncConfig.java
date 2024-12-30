// src/main/java/com/example/receipt_backend/config/AsyncConfig.java
package com.example.receipt_backend.config;

import com.example.receipt_backend.utils.TenantAwareTaskDecorator;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    private final TenantAwareTaskDecorator taskDecorator;

    public AsyncConfig(TenantAwareTaskDecorator taskDecorator) {
        this.taskDecorator = taskDecorator;
    }

    @Bean(name = "taskExecutor")
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(25);
        executor.setTaskDecorator(taskDecorator);
        executor.setThreadNamePrefix("OCR-Executor-");
        executor.initialize();
        return executor;
    }
}
