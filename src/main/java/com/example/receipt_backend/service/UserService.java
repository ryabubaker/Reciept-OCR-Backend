package com.example.receipt_backend.service;
import com.example.receipt_backend.dto.UserDTO;
import com.example.receipt_backend.dto.request.*;
import com.example.receipt_backend.dto.response.GenericResponseDTO;
import com.example.receipt_backend.entity.User;
import com.example.receipt_backend.utils.RoleType;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {

    // CRUD
    List<UserDTO> getAllUsers(Pageable pageable);

    UserDTO findUserByEmail(String email);

    Optional<UserDTO> findOptionalUserByEmail(String email);

    UserDTO getUserById(UUID id);


    User createUser(UserDTO userDto, String tenantId, RoleType roleType);

    UserDTO updateUser(UUID id, UserDTO userDTO);

    // Email Verification
    GenericResponseDTO<Boolean> sendVerificationEmail(String email);

    GenericResponseDTO<Boolean> verifyEmailAddress(VerifyEmailRequestDTO verifyEmailRequestDTO);

    // Reset Password
    GenericResponseDTO<Boolean> sendResetPasswordEmail(ForgotPasswordRequestDTO forgotPasswordRequestDTO);

    GenericResponseDTO<Boolean> verifyAndProcessPasswordResetRequest(ResetPasswordRequestDTO resetPasswordRequestDTO);

    // Other extras
    GenericResponseDTO<Boolean> userEmailExists(String email);

    GenericResponseDTO<Boolean> updatePassword(UpdatePasswordRequestDTO updatePasswordRequest);

    ResponseEntity<GenericResponseDTO<Boolean>> createUserByAdmin(RegisterUserByAdminDto request);

    void deleteUsers(List<UUID> userIds);
}
