package com.example.receipt_backend.entity.common;
import com.example.receipt_backend.entity.User;
import com.example.receipt_backend.repository.UserRepository;
import com.example.receipt_backend.security.AppSecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("auditorAwareUserImpl")
public class AuditorAwareUserImpl implements AuditorAware<User> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public Optional<User> getCurrentAuditor() {
        Optional<Long> optionalUserId = Optional
                .ofNullable(AppSecurityUtils.getCurrentUserPrinciple())
                .map(e -> e.getUser().getId());
        Optional<User> userEntity = optionalUserId.map(userId -> userRepository.getById(userId));
        return userEntity;
    }

}