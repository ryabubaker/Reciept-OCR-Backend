package com.example.receipt_backend.service.impl;

import com.example.receipt_backend.config.AppProperties;
import com.example.receipt_backend.config.multitenant.CurrentTenantIdentifierResolverImpl;
import com.example.receipt_backend.dto.UserDTO;
import com.example.receipt_backend.dto.request.*;
import com.example.receipt_backend.dto.response.GenericResponseDTO;
import com.example.receipt_backend.entity.Tenant;
import com.example.receipt_backend.entity.User;
import com.example.receipt_backend.exception.CustomAppException;
import com.example.receipt_backend.exception.ErrorCode;
import com.example.receipt_backend.exception.ResourceNotFoundException;
import com.example.receipt_backend.mail.EmailService;
import com.example.receipt_backend.mapper.UserMapper;
import com.example.receipt_backend.repository.TenantRepository;
import com.example.receipt_backend.repository.UserRepository;
import com.example.receipt_backend.security.SecurityEnums;
import com.example.receipt_backend.service.UserService;
import com.example.receipt_backend.utils.AppUtils;
import com.example.receipt_backend.utils.RoleType;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final EmailService emailService;
    private final AppProperties appProperties;
    private final TenantRepository tenantRepository;

    @Override
    public List<UserDTO> getAllUsers(Pageable pageable) {
        Page<User> pageUserEntities = userRepository.findAll(pageable);
        return userMapper.toDtoList(pageUserEntities.getContent());
    }

    @Override
    public UserDTO findUserByEmail(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_RECORD_NOT_FOUND));
        return userMapper.toDto(user);
    }

    @Override
    public Optional<UserDTO> findOptionalUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::toDto);
    }

    @Override
    public UserDTO getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException( ErrorCode.USER_RECORD_NOT_FOUND));
        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    public User createUser(UserDTO userDto, String tenantId, RoleType roleType) {
        if (userRepository.existsByEmailAndTenant_TenantId(userDto.getEmail(), UUID.fromString(tenantId))) {
            throw new CustomAppException(ErrorCode.USER_ALREADY_EXISTS);
        }

        checkUsernameAvailability(userDto.getUsername(), tenantId);

        boolean isFromCustomBasicAuth = SecurityEnums.AuthProviderId.local.equals(userDto.getRegisteredProviderName());
        if (isFromCustomBasicAuth && userDto.getPassword() != null) {
            userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }

        if (ObjectUtils.isEmpty(userDto.getRole())) {
            userDto.setRole(roleType.toString());
        }

        User user = userMapper.toEntity(userDto);

        Tenant tenant = tenantRepository.findByTenantId(UUID.fromString(tenantId))
                .orElseThrow(() -> new ResourceNotFoundException( ErrorCode.TENANT_NOT_FOUND));

        user.setTenant(tenant);

        // Save the user
        userRepository.save(user);

        // If the role is ROLE_COMPANY_ADMIN, add the user to the list of admin users in the tenant
        if (RoleType.ROLE_COMPANY_ADMIN.equals(roleType)) {
            List<User> adminUsers = tenant.getAdminUsers();
            if (adminUsers == null) {
                adminUsers = new ArrayList<>();
            }
            adminUsers.add(user);
            tenant.setAdminUsers(adminUsers);
            tenantRepository.save(tenant);
        }

        if (!user.isEmailVerified()) {
            sendVerificationEmail(user.getEmail());
        }

        return user;
    }

    @Override
    public UserDTO updateUser(UUID id, UserDTO reqUserDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_RECORD_NOT_FOUND));
        reqUserDTO.setPassword(passwordEncoder.encode(reqUserDTO.getPassword()));
        reqUserDTO.setEmailVerified(true);
        reqUserDTO.setRegisteredProviderName(SecurityEnums.AuthProviderId.local);

        userMapper.updateEntity(reqUserDTO, user);
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    public GenericResponseDTO<Boolean> sendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_RECORD_NOT_FOUND));
        String verificationCode = AppUtils.generateRandomAlphaNumericString(20);
        long verificationCodeExpirationSeconds = appProperties.getMail().getVerificationCodeExpirationSeconds();
        user.setVerificationCodeExpiresAt(Instant.now().plusSeconds(verificationCodeExpirationSeconds));
        user.setVerificationCode(verificationCode);
        MultiValueMap<String, String> appendQueryParamsToVerificationLink = constructEmailVerificationLinkQueryParams(
                user.getEmail(), verificationCode, user.getRegisteredProviderName());
        String fullName = user.getUsername();
        String firstName = fullName.contains(" ") ? fullName.split(" ", 2)[0] : fullName;
        userRepository.save(user);
        emailService.sendVerificationEmail(user.getEmail(), firstName, appendQueryParamsToVerificationLink);
        return GenericResponseDTO.<Boolean>builder().response(true).build();
    }

    @Override
    public GenericResponseDTO<Boolean> sendResetPasswordEmail(ForgotPasswordRequestDTO forgotPasswordRequestDTO) {
        User user = userRepository.findByEmail(forgotPasswordRequestDTO.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_EMAIL_NOT_AVAILABLE));
        String forgotPasswordVerCode = AppUtils.generateRandomAlphaNumericString(20);
        long verificationCodeExpirationSeconds = appProperties.getMail().getVerificationCodeExpirationSeconds();
        user.setVerificationCodeExpiresAt(Instant.now().plusSeconds(verificationCodeExpirationSeconds));
        user.setVerificationCode(forgotPasswordVerCode);
        MultiValueMap<String, String> appendQueryParamsToPasswordResetLink = constructPasswordResetLinkQueryParams(
                user.getEmail(), forgotPasswordVerCode);
        String fullName = user.getUsername();
        String firstName = fullName.contains(" ") ? fullName.split(" ", 2)[0] : fullName;
        userRepository.save(user);
        emailService.sendPasswordResetEmail(user.getEmail(), firstName, appendQueryParamsToPasswordResetLink);
        return GenericResponseDTO.<Boolean>builder().response(true).build();
    }

    @Override
    public GenericResponseDTO<Boolean> verifyEmailAddress(VerifyEmailRequestDTO verifyEmailRequestDTO) {
        Optional<User> optionalUserEntity = userRepository.verifyAndRetrieveEmailVerificationRequestUser(
                verifyEmailRequestDTO.getEmail(), verifyEmailRequestDTO.getAuthProviderId(), verifyEmailRequestDTO.getVerificationCode());
        User user = optionalUserEntity
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.MATCHING_VERIFICATION_RECORD_NOT_FOUND));
        user.setEmailVerified(Boolean.TRUE);
        user.setVerificationCodeExpiresAt(null);
        user.setVerificationCode(null);
        userRepository.save(user);
        return GenericResponseDTO.<Boolean>builder().response(true).build();
    }

    @Override
    public GenericResponseDTO<Boolean> verifyAndProcessPasswordResetRequest(ResetPasswordRequestDTO resetPasswordRequestDTO) {
        Optional<User> optionalUserEntity = userRepository.verifyAndRetrieveForgotPasswordRequestUser(
                resetPasswordRequestDTO.getEmail(), SecurityEnums.AuthProviderId.local, resetPasswordRequestDTO.getForgotPasswordVerCode());
        User user = optionalUserEntity
                .orElseThrow(() -> new CustomAppException(ErrorCode.INVALID_PASSWORD_RESET_REQUEST));
        user.setVerificationCodeExpiresAt(null);
        user.setVerificationCode(null);
        user.setEmailVerified(true);
        user.setPassword(passwordEncoder.encode(resetPasswordRequestDTO.getNewPassword()));
        userRepository.save(user);
        return GenericResponseDTO.<Boolean>builder().response(true).build();
    }

    @Override
    public GenericResponseDTO<Boolean> userEmailExists(String email) {
        boolean existsByEmail = userRepository.existsByEmail(email);
        return GenericResponseDTO.<Boolean>builder().response(existsByEmail).build();
    }

    @Override
    public GenericResponseDTO<Boolean> updatePassword(UpdatePasswordRequestDTO updatePasswordRequest) {
        User user = userRepository.findById(updatePasswordRequest.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_RECORD_NOT_FOUND));
        boolean passwordMatches = passwordEncoder.matches(updatePasswordRequest.getOldPassword(), user.getPassword());
        if (!passwordMatches) {
            throw new CustomAppException(ErrorCode.OLD_PASSWORD_DOESNT_MATCH);
        }
        user.setPassword(passwordEncoder.encode(updatePasswordRequest.getNewPassword()));
        userRepository.save(user);
        return GenericResponseDTO.<Boolean>builder().response(true).build();
    }

    @Override
    public ResponseEntity<GenericResponseDTO<Boolean>> createUserByAdmin(RegisterUserByAdminDto request) {
        String tenantName = CurrentTenantIdentifierResolverImpl.getTenant();
        Tenant tenant = tenantRepository.findByTenantName(tenantName);

        if (tenant == null) {
            throw new CustomAppException(ErrorCode.TENANT_NOT_FOUND);
        }

        String tenantId = tenant.getTenantId().toString();

        RoleType roleType;
        try {
            roleType = RoleType.valueOf(request.getRoleType());
        } catch (IllegalArgumentException e) {
            throw new CustomAppException(ErrorCode.INVALID_INPUT, "Invalid role type provided.");
        }

        // Use provided password instead of generating
        String providedPassword = request.getPassword();
        if (providedPassword == null || providedPassword.isEmpty()) {
            throw new CustomAppException(ErrorCode.PASSWORD_EMPTY);
        }

        // Build UserDTO with the correct tenantId
        UserDTO adminUserDto = UserDTO.builder()
                .id(UUID.fromString(request.getUserId()))
                .username(request.getUsername())
                .email(request.getEmail())
                .emailVerified(true)
                .password(providedPassword)
                .registeredProviderName(SecurityEnums.AuthProviderId.local)
                .role(request.getRoleType())
                .tenantId(tenantId) // Use tenantId from the saved tenant
                .build();

        // Create the admin user
        User createdUser = createUser(adminUserDto, tenantId, roleType);

        // Verify user creation
        userRepository.findById(createdUser.getId())
                .orElseThrow(() -> new CustomAppException( ErrorCode.USER_RECORD_NOT_FOUND));

        // Send a welcome email with the generated password
        emailService.sendWelcomeEmailWithPassword(createdUser.getEmail(), createdUser.getEmail(), providedPassword);

        // Return success response
        return new ResponseEntity<>(GenericResponseDTO.<Boolean>builder().response(true).build(), HttpStatus.OK);
    }

    @Override
    @Transactional
    public void deleteUsers(List<UUID> userIds) {
        List<User> users = userRepository.findAllById(userIds);
        if (users.size() != userIds.size()) {
            throw new ResourceNotFoundException("Some users not found.");
        }
        userRepository.deleteAll(users);
    }



    private static MultiValueMap<String, String> constructEmailVerificationLinkQueryParams(String email,
                                                                                           String verificationCode,
                                                                                           SecurityEnums.AuthProviderId authProvider) {
        MultiValueMap<String, String> appendQueryParams = new LinkedMultiValueMap<>();
        // Generated QueryParams for the verification link, must sync with VerifyEmailRequestDTO
        appendQueryParams.add("email", email);
        appendQueryParams.add("registeredProviderName", authProvider.toString());
        appendQueryParams.add("verificationCode", verificationCode);
        return appendQueryParams;
    }

    private static MultiValueMap<String, String> constructPasswordResetLinkQueryParams(String email,
                                                                                       String forgotPasswordVerCode) {
        MultiValueMap<String, String> appendQueryParams = new LinkedMultiValueMap<>();
        // Generated QueryParams for the password reset link, must sync with ResetPasswordRequestDTO
        appendQueryParams.add("email", email);
        appendQueryParams.add("forgotPasswordVerCode", forgotPasswordVerCode);
        return appendQueryParams;
    }

    private void checkUsernameAvailability(String username, String tenantId) {
        Optional<User> existingUserOpt = userRepository.findByUsernameAndTenant_TenantId(username, UUID.fromString(tenantId));
        if (existingUserOpt.isPresent()) {
            // Already exists, throw a custom exception
            throw new CustomAppException( ErrorCode.USER_USERNAME_NOT_AVAILABLE);
        }
    }

}
