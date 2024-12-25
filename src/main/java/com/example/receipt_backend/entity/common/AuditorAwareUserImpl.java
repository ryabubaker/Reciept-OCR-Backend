package com.example.receipt_backend.entity.common;
import com.example.receipt_backend.entity.User;
import com.example.receipt_backend.repository.UserRepository;
import com.example.receipt_backend.security.AppSecurityUtils;
import com.example.receipt_backend.security.CustomUserDetails;
import io.micrometer.common.lang.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component("auditorAwareUserImpl")
public class AuditorAwareUserImpl implements AuditorAware<User> {

    @Autowired
    private UserRepository userRepository;

    @Override
    @NonNull
    public Optional<User> getCurrentAuditor() {
        Optional<UUID> optionalUserId = Optional
                .ofNullable(AppSecurityUtils.getCurrentUserPrinciple())
                .map(e -> e.getUser().getId());
        return optionalUserId.map(userId -> userRepository.getReferenceById(userId));
    }

}