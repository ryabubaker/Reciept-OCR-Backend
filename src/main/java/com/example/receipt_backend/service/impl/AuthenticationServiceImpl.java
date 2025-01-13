package com.example.receipt_backend.service.impl;

import com.example.receipt_backend.dto.UserDTO;
import com.example.receipt_backend.dto.request.LoginRequestDTO;
import com.example.receipt_backend.dto.request.RegisterRequest;
import com.example.receipt_backend.dto.request.RegisterUserRequestDTO;
import com.example.receipt_backend.dto.response.AuthResponseDTO;
import com.example.receipt_backend.dto.response.GenericResponseDTO;
import com.example.receipt_backend.exception.BadRequestException;
import com.example.receipt_backend.mapper.UserMapper;
import com.example.receipt_backend.security.CustomUserDetails;
import com.example.receipt_backend.security.JwtUtils;
import com.example.receipt_backend.security.SecurityEnums;
import com.example.receipt_backend.security.UserDetailsServiceImpl;
import com.example.receipt_backend.service.AuthenticationService;
import com.example.receipt_backend.service.InvitationService;
import com.example.receipt_backend.service.UserService;
import com.example.receipt_backend.utils.RoleType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;

    private final UserMapper userMapper;
    private final InvitationService invitationService;
    private final JwtUtils jwtUtils;
    private final UserService userService;
    private final UserDetailsServiceImpl userDetailsService;



    @Override
    public GenericResponseDTO<Boolean> registerUser(RegisterUserRequestDTO request) {
        try {

            // Proceed with user registration
            UserDTO userDTO = userMapper.toUserDTO(request);
            userService.createUser(userDTO, request.getTenantId(), request.getRoleType());
            return GenericResponseDTO.<Boolean>builder().response(true).build();
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "An error occurred during registration: " + e.getMessage());
        }
    }
@Override
public GenericResponseDTO<String> registerUserWithInvitation(RegisterRequest registerRequest) {
    String token = registerRequest.getInvitationToken();
    String email = registerRequest.getEmail();
    InvitationServiceImpl.InvitationDetails details = invitationService.validateInvitation(token);

    if (details == null) {
        throw new IllegalArgumentException("Invalid or expired invitation code.");
    }

    if (!details.getEmail().equalsIgnoreCase(email)) {
        throw new IllegalArgumentException("Email does not match the invitation.");
    }

    try {
        // Create user
        UserDTO userDto = new UserDTO();
        userDto.setEmail(email);
        userDto.setUsername(registerRequest.getUsername());
        userDto.setRegisteredProviderName(SecurityEnums.AuthProviderId.local);
        userDto.setEmailVerified(true);
        userDto.setRole(String.valueOf(details.getRole()));
        userDto.setTenantId(String.valueOf(details.getTenantId()));
        userDto.setPassword(registerRequest.getPassword());

        // Register user
        userService.createUser(userDto, String.valueOf(details.getTenantId()), RoleType.ROLE_MOBILE_USER);

    } catch (BadRequestException e) {
        throw  new BadRequestException("Failed to register user"  );
    }
    return null;
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
            String accessToken = jwtUtils.generateAccessToken(userDetails);
            String refreshToken = jwtUtils.generateRefreshToken(userDetails);
            return new AuthResponseDTO(accessToken, refreshToken);
        } catch (BadCredentialsException e) {
            throw new BadRequestException("Invalid email or password");
        } catch (Exception e) {
            throw new BadRequestException("An error occurred during login: " + e.getMessage());
        }
    }
    @Override
    public AuthResponseDTO refresh(String refreshToken) {

        // 1) Validate Refresh Token
        if (!jwtUtils.validateJwtToken(refreshToken) || !jwtUtils.isRefreshToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        // 2) Extract username from token
        String username = jwtUtils.getUsernameFromJwt(refreshToken);

        // 3) Load user details
        CustomUserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // 4) Generate new Access Token
        String newAccessToken = jwtUtils.generateAccessToken(userDetails);

        // Optionally generate a fresh refresh token as well:
        String newRefreshToken = jwtUtils.generateRefreshToken(userDetails);

        return new AuthResponseDTO(newAccessToken, newRefreshToken);
    }


}


