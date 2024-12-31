package com.example.receipt_backend.service.impl;

import com.example.receipt_backend.config.multitenant.CurrentTenantIdentifierResolverImpl;
import com.example.receipt_backend.dto.UserDTO;
import com.example.receipt_backend.dto.request.LoginRequestDTO;
import com.example.receipt_backend.dto.request.RegisterUserRequestDTO;
import com.example.receipt_backend.dto.response.AuthResponseDTO;
import com.example.receipt_backend.entity.RoleEntity;
import com.example.receipt_backend.entity.Tenant;
import com.example.receipt_backend.entity.User;
import com.example.receipt_backend.exception.BadRequestException;
import com.example.receipt_backend.mapper.UserMapper;
import com.example.receipt_backend.repository.RoleRepository;
import com.example.receipt_backend.repository.TenantRepository;
import com.example.receipt_backend.repository.UserRepository;
import com.example.receipt_backend.security.CustomUserDetails;
import com.example.receipt_backend.security.JwtUtils;
import com.example.receipt_backend.service.AuthenticationService;
import com.example.receipt_backend.service.UserService;
import com.example.receipt_backend.utils.RoleType;
import com.nimbusds.openid.connect.sdk.AuthenticationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;

    private final UserMapper userMapper;

    private final JwtUtils jwtUtils;
    private final UserService userService;

    public AuthenticationServiceImpl(AuthenticationManager authenticationManager, UserMapper userMapper, JwtUtils jwtUtils, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.userMapper = userMapper;
        this.jwtUtils = jwtUtils;
        this.userService = userService;
    }

    @Override
    public UserDTO registerUser(RegisterUserRequestDTO request) {
        UserDTO userDTO = userMapper.toUserDTO(request);
        User user = userService.createUser(userDTO, request.getTenantId(), request.getRoleType());
        return userMapper.toDto(user);
    }
    @Override
    @Transactional
    public AuthResponseDTO loginUser(LoginRequestDTO loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            String jwtToken = jwtUtils.generateJwtToken(userDetails);
            return new AuthResponseDTO(jwtToken);
        } catch (BadCredentialsException e) {
            throw new BadRequestException("Invalid email or password");
        } catch (Exception e) {
            throw new BadRequestException("An error occurred during login: " + e.getMessage());
        }
    }
}
