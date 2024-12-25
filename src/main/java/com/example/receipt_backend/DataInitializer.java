package com.example.receipt_backend;

import com.example.receipt_backend.config.multitenant.CurrentTenantIdentifierResolverImpl;
import com.example.receipt_backend.entity.RoleEntity;
import com.example.receipt_backend.entity.User;
import com.example.receipt_backend.exception.ResourceNotFoundException;
import com.example.receipt_backend.mapper.UserMapper;
import com.example.receipt_backend.repository.RoleRepository;
import com.example.receipt_backend.repository.UserRepository;
import com.example.receipt_backend.security.SecurityEnums;
import com.example.receipt_backend.utils.AppUtils;
import com.example.receipt_backend.utils.RoleType;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.receipt_backend.service.UserService;
import com.example.receipt_backend.dto.UserDTO;


import java.util.Set;

import static com.example.receipt_backend.config.multitenant.CurrentTenantIdentifierResolverImpl.setTenant;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository roleRepository;



    @Override
    public void run(String... args) throws Exception {
        if (!userRepository.existsByRoles_Name(RoleType.ROLE_SYSTEM_ADMIN)) {

            UserDTO systemAdmin = UserDTO.builder()
                    .username("admin")
                    .email("admin@example.com")
                    .password("123456")
                    .registeredProviderName(SecurityEnums.AuthProviderId.local)
                    .roles(Set.of("ROLE_SYSTEM_ADMIN"))
                    .emailVerified(true)
                    .build();

            User user = userMapper.toEntity(systemAdmin);
            user.setPassword(passwordEncoder.encode(systemAdmin.getPassword()));
            userRepository.save(user);
        } else {
            System.out.println("System admin user already exists.");
        }
    }
}