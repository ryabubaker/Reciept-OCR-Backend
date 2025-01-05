package com.example.receipt_backend.service.impl;

import com.example.receipt_backend.dto.UserDTO;
import com.example.receipt_backend.dto.request.LoginRequestDTO;
import com.example.receipt_backend.dto.request.RegisterUserRequestDTO;
import com.example.receipt_backend.dto.response.AuthResponseDTO;
import com.example.receipt_backend.dto.response.GenericResponseDTO;
import com.example.receipt_backend.entity.User;
import com.example.receipt_backend.exception.BadRequestException;
import com.example.receipt_backend.mapper.UserMapper;
import com.example.receipt_backend.security.CustomUserDetails;
import com.example.receipt_backend.security.JwtUtils;
import com.example.receipt_backend.service.AuthenticationService;
import com.example.receipt_backend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public GenericResponseDTO<Boolean> registerUser(RegisterUserRequestDTO request) {
        UserDTO userDTO = userMapper.toUserDTO(request);
        userService.createUser(userDTO, request.getTenantId(), request.getRoleType());
        return GenericResponseDTO.<Boolean>builder().response(true).build();
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
