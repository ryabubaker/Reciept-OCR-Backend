package com.example.receipt_backend.utils;

import com.example.receipt_backend.config.multitenant.CurrentTenantIdentifierResolverImpl;
import org.springframework.core.task.TaskDecorator;
import org.springframework.stereotype.Component;

@Component
public class TenantAwareTaskDecorator implements TaskDecorator {

    @Override
    public Runnable decorate(Runnable runnable) {
        String tenant = CurrentTenantIdentifierResolverImpl.getTenant();
        return () -> {
                CurrentTenantIdentifierResolverImpl.setTenant(tenant);
                runnable.run();

        };
    }
}
