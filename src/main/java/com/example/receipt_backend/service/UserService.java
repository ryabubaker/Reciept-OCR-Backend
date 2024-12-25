package com.example.receipt_backend.service;
import com.example.receipt_backend.dto.UserDTO;
import com.example.receipt_backend.dto.request.ForgotPasswordRequestDTO;
import com.example.receipt_backend.dto.request.ResetPasswordRequestDTO;
import com.example.receipt_backend.dto.request.UpdatePasswordRequestDTO;
import com.example.receipt_backend.dto.request.VerifyEmailRequestDTO;
import com.example.receipt_backend.dto.response.GenericResponseDTO;
import com.example.receipt_backend.entity.User;
import com.example.receipt_backend.utils.RoleType;
import org.springframework.data.domain.Pageable;

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

    UserDTO updateUser(UserDTO userDTO);

    // Email Verification
    GenericResponseDTO<Boolean> sendVerificationEmail(String email);

    GenericResponseDTO<Boolean> verifyEmailAddress(VerifyEmailRequestDTO verifyEmailRequestDTO);

    // Reset Password
    GenericResponseDTO<Boolean> sendResetPasswordEmail(ForgotPasswordRequestDTO forgotPasswordRequestDTO);

    GenericResponseDTO<Boolean> verifyAndProcessPasswordResetRequest(ResetPasswordRequestDTO resetPasswordRequestDTO);

    // Other extras
    GenericResponseDTO<Boolean> userEmailExists(String email);

    GenericResponseDTO<Boolean> updatePassword(UpdatePasswordRequestDTO updatePasswordRequest);

}
