package com.example.receipt_backend.security;

import com.example.receipt_backend.entity.UserEntity;
import com.example.receipt_backend.exception.AppExceptionConstants;
import com.example.receipt_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(AppExceptionConstants.BAD_LOGIN_CREDENTIALS));
        return CustomUserDetails.buildFromUserEntity(userEntity);
    }
}