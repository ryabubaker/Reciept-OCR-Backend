package com.example.receipt_backend.service.impl;

import com.example.receipt_backend.config.multitenant.CurrentTenantIdentifierResolverImpl;
import com.example.receipt_backend.dto.UserDTO;
import com.example.receipt_backend.dto.request.*;
import com.example.receipt_backend.dto.response.GenericResponseDTO;
import com.example.receipt_backend.entity.RoleEntity;
import com.example.receipt_backend.entity.Tenant;
import com.example.receipt_backend.entity.User;
import com.example.receipt_backend.exception.AppExceptionConstants;
import com.example.receipt_backend.exception.BadRequestException;
import com.example.receipt_backend.exception.CustomAppException;
import com.example.receipt_backend.exception.ResourceNotFoundException;
import com.example.receipt_backend.mapper.UserMapper;
import com.example.receipt_backend.repository.RoleRepository;
import com.example.receipt_backend.repository.TenantRepository;
import com.example.receipt_backend.repository.UserRepository;
import com.example.receipt_backend.security.SecurityEnums;
import com.example.receipt_backend.service.UserService;
import com.example.receipt_backend.mail.EmailService;
import com.example.receipt_backend.config.AppProperties;
import com.example.receipt_backend.utils.AppUtils;
import com.example.receipt_backend.utils.RoleType;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;

import java.time.Instant;
import java.util.*;

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
    private final RoleRepository roleRepository;

    @Override
    public List<UserDTO> getAllUsers(Pageable pageable) {
        Page<User> pageUserEntities = userRepository.findAll(pageable);
        return userMapper.toDtoList(pageUserEntities.getContent());
    }

    @Override
    public UserDTO findUserByEmail(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException(AppExceptionConstants.USER_RECORD_NOT_FOUND));
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
                .orElseThrow(() -> new ResourceNotFoundException(AppExceptionConstants.USER_RECORD_NOT_FOUND));
        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    public User createUser(UserDTO userDto, String tenantId, RoleType roleType) {
        if (userRepository.existsByEmailAndTenant_TenantId(userDto.getEmail(), UUID.fromString(tenantId))) {
            throw new CustomAppException("User already exists for this tenant.");
        }
        boolean isFromCustomBasicAuth = SecurityEnums.AuthProviderId.local.equals(userDto.getRegisteredProviderName());
        if (isFromCustomBasicAuth && userDto.getPassword() != null) {
            userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }

        if (ObjectUtils.isEmpty(userDto.getRoles())) {
            userDto.setRoles(Set.of(roleType.toString()));
        }

        User user = userMapper.toEntity(userDto);



        Tenant tenant = tenantRepository.findByTenantId(UUID.fromString(tenantId))
                .orElseThrow(() -> new ResourceNotFoundException(AppExceptionConstants.TENANT_NOT_FOUND));

        user.setTenant(tenant);

        userRepository.save(user);

        if (!user.isEmailVerified()){
            sendVerificationEmail(user.getEmail());
        }

        return user;
    }

    @Override
    public UserDTO updateUser(UserDTO reqUserDTO) {
        User user = userRepository.findById(reqUserDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException(AppExceptionConstants.USER_RECORD_NOT_FOUND));

        userMapper.updateEntity(reqUserDTO, user);
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    @Override
//    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public GenericResponseDTO<Boolean> sendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(AppExceptionConstants.USER_RECORD_NOT_FOUND));
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
        GenericResponseDTO<Boolean> genericResponseDTO = GenericResponseDTO.<Boolean>builder().response(true).build();
        return genericResponseDTO;
    }

    @Override
    public GenericResponseDTO<Boolean> sendResetPasswordEmail(ForgotPasswordRequestDTO forgotPasswordRequestDTO) {
        User user = userRepository.findByEmail(forgotPasswordRequestDTO.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException(AppExceptionConstants.USER_EMAIL_NOT_AVAILABLE));
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
        GenericResponseDTO<Boolean> genericResponseDTO = GenericResponseDTO.<Boolean>builder().response(true).build();
        return genericResponseDTO;
    }

    @Override
    public GenericResponseDTO<Boolean> verifyEmailAddress(VerifyEmailRequestDTO verifyEmailRequestDTO) {
        Optional<User> optionalUserEntity = userRepository.verifyAndRetrieveEmailVerificationRequestUser(
                verifyEmailRequestDTO.getEmail(), verifyEmailRequestDTO.getAuthProviderId(), verifyEmailRequestDTO.getVerificationCode());
        User user = optionalUserEntity
                .orElseThrow(() -> new ResourceNotFoundException(AppExceptionConstants.MATCHING_VERIFICATION_RECORD_NOT_FOUND));
        user.setEmailVerified(Boolean.TRUE);
        user.setVerificationCodeExpiresAt(null);
        user.setVerificationCode(null);
        userRepository.save(user);
        GenericResponseDTO<Boolean> emailVerifiedResponseDTO = GenericResponseDTO.<Boolean>builder().response(true).build();
        return emailVerifiedResponseDTO;
    }

    @Override
    public GenericResponseDTO<Boolean> verifyAndProcessPasswordResetRequest(ResetPasswordRequestDTO resetPasswordRequestDTO) {
        Optional<User> optionalUserEntity = userRepository.verifyAndRetrieveForgotPasswordRequestUser(
                resetPasswordRequestDTO.getEmail(), SecurityEnums.AuthProviderId.local, resetPasswordRequestDTO.getForgotPasswordVerCode());
        User user = optionalUserEntity
                .orElseThrow(() -> new ResourceNotFoundException(AppExceptionConstants.INVALID_PASSWORD_RESET_REQUEST));
        user.setVerificationCodeExpiresAt(null);
        user.setVerificationCode(null);
        user.setEmailVerified(true);
        user.setPassword(passwordEncoder.encode(resetPasswordRequestDTO.getNewPassword()));
        userRepository.save(user);
        GenericResponseDTO<Boolean> emailVerifiedResponseDTO = GenericResponseDTO.<Boolean>builder().response(true).build();
        return emailVerifiedResponseDTO;
    }

    @Override
    public GenericResponseDTO<Boolean> userEmailExists(String email) {
        boolean existsByEmail = userRepository.existsByEmail(email);
        return GenericResponseDTO.<Boolean>builder().response(existsByEmail).build();
    }

    @Override
    public GenericResponseDTO<Boolean> updatePassword(UpdatePasswordRequestDTO updatePasswordRequest) {
        User user = userRepository.findById(updatePasswordRequest.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(AppExceptionConstants.USER_RECORD_NOT_FOUND));
        boolean passwordMatches = passwordEncoder.matches(updatePasswordRequest.getOldPassword(), user.getPassword());
        if (!passwordMatches) {
            throw new BadRequestException(AppExceptionConstants.OLD_PASSWORD_DOESNT_MATCH);
        }
        user.setPassword(passwordEncoder.encode(updatePasswordRequest.getNewPassword()));
        userRepository.save(user);
        return GenericResponseDTO.<Boolean>builder().response(true).build();
    }

    @Override
    public void createUserByAdmin(RegisterUserByAdminDto request) {
        String tenantName = CurrentTenantIdentifierResolverImpl.getTenant();
        String tenantId = tenantRepository.findByTenantName(tenantName).getTenantId().toString();

        RoleType roleType = RoleType.valueOf(request.getRoleType());


        String generatedPassword = AppUtils.generateRandomAlphaNumericString(10);

            // Build UserDTO with the correct tenantId
            UserDTO adminUserDto = UserDTO.builder()
                    .username(request.getEmail())
                    .email(request.getEmail())
                    .emailVerified(true)
                    .password(generatedPassword)
                    .registeredProviderName(SecurityEnums.AuthProviderId.local)
                    .roles(Set.of(request.getRoleType()))
                    .tenantId(tenantId) // Use tenantId from the saved tenant
                    .build();

            // Create the admin user
            User createdUser = createUser(adminUserDto, tenantId, roleType);
            System.out.println("Admin user created with ID: " + createdUser.getId());
            userRepository.findById(createdUser.getId()).orElseThrow(() -> new ResourceNotFoundException(AppExceptionConstants.USER_RECORD_NOT_FOUND));

            // Send a welcome email with the generated password
            emailService.sendWelcomeEmailWithPassword(createdUser.getEmail(), createdUser.getEmail(), generatedPassword);
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

}
