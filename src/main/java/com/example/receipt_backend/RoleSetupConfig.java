//package com.example.receipt_backend;
//
//import com.example.receipt_backend.entity.RoleEntity;
//import com.example.receipt_backend.repository.RoleRepository;
//import com.example.receipt_backend.utils.RoleType;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class RoleSetupConfig {
//
//    @Bean
//    public CommandLineRunner setupDefaultRoles(RoleRepository roleRepository) {
//        return args -> {
//            // Ensure ROLE_USER exists
//            if (roleRepository.findByName(RoleType.ROLE_USER).isEmpty()) {
//                roleRepository.save(new RoleEntity(RoleType.ROLE_USER));
//            }
//
//            // Ensure ROLE_ADMIN exists
//            if (roleRepository.findByName(RoleType.ROLE_COMPANY_ADMIN).isEmpty()) {
//                roleRepository.save(new RoleEntity( RoleType.ROLE_COMPANY_ADMIN));
//            }
//
//            // Add other roles as needed
//            if (roleRepository.findByName(RoleType.ROLE_SYSTEM_ADMIN).isEmpty()) {
//                roleRepository.save(new RoleEntity(RoleType.ROLE_SYSTEM_ADMIN));
//            }
//
//            if (roleRepository.findByName(RoleType.ROLE_DATA_ANALYST).isEmpty()) {
//                roleRepository.save(new RoleEntity( RoleType.ROLE_DATA_ANALYST));
//            }
//        };
//    }
//}
