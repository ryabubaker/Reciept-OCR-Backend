package com.example.receipt_backend.service.impl;
import com.example.receipt_backend.dto.UserDTO;
import com.example.receipt_backend.dto.request.LoginRequestDTO;
import com.example.receipt_backend.dto.request.RegisterUserRequestDTO;
import com.example.receipt_backend.dto.response.AuthResponseDTO;
import com.example.receipt_backend.exception.AppExceptionConstants;
import com.example.receipt_backend.mapper.UserMapper;
import com.example.receipt_backend.security.JWTTokenProvider;
import com.example.receipt_backend.security.oauth.common.SecurityEnums;
import com.example.receipt_backend.service.AuthenticationService;
import com.example.receipt_backend.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JWTTokenProvider jwtTokenProvider;
    private final UserMapper userMapper;

    public AuthenticationServiceImpl(AuthenticationManager authenticationManager,
                                     UserService userService,
                                     JWTTokenProvider jwtTokenProvider, UserMapper userMapper) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userMapper = userMapper;
    }


    @Override
    public AuthResponseDTO loginUser(LoginRequestDTO loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
            String token = jwtTokenProvider.createJWTToken(authentication);
            AuthResponseDTO authResponseDTO = new AuthResponseDTO();
            authResponseDTO.setToken(token);
            return authResponseDTO;
        } catch (AuthenticationException e) {
            if (e instanceof DisabledException) {
                throw new BadCredentialsException(AppExceptionConstants.ACCOUNT_NOT_ACTIVATED);
            }
            throw new BadCredentialsException(e.getMessage());
        }
    }

    @Override
    public UserDTO registerUser(RegisterUserRequestDTO request) {
        UserDTO userDTO = userMapper.toUserDTO(request);
        UserDTO user = userService.createUser(userDTO, request.getTenantId(),request.getRoleType() );
        return user;
    }

}
