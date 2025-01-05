package com.example.receipt_backend.controller;

import com.example.receipt_backend.dto.UserDTO;
import com.example.receipt_backend.dto.request.RegisterUserByAdminDto;
import com.example.receipt_backend.dto.request.UpdatePasswordRequestDTO;
import com.example.receipt_backend.dto.response.GenericResponseDTO;
import com.example.receipt_backend.entity.User;
import com.example.receipt_backend.mapper.UserMapper;
import com.example.receipt_backend.security.AppSecurityUtils;
import com.example.receipt_backend.service.UserService;
import com.example.receipt_backend.utils.RoleType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("users")
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "APIs for managing users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping("admin-create")
    @PreAuthorize("hasRole('ROLE_COMPANY_ADMIN')")
    @Operation(summary = "Create a new user by tenant admin", description = "Create a new user with the specified details")

    public ResponseEntity<GenericResponseDTO<Boolean>> createUserByAdmin(@RequestBody RegisterUserByAdminDto userDTO) {
        GenericResponseDTO<Boolean> response = userService.createUserByAdmin(userDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUser(Pageable pageable) {
        log.info("User API: get all user");
        List<UserDTO> userDTOList = userService.getAllUsers(pageable);
        return new ResponseEntity<>(userDTOList, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieve a user's details by their ID")
    public ResponseEntity<UserDTO> getUserById(@PathVariable UUID id) {
        log.info("User API: get user by id: ", id);
        UserDTO userDTO = userService.getUserById(id);
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    @PostMapping
    @Operation(summary = "Create a new user", description = "Create a new user with the specified details")
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO, @RequestParam String tenantId, @RequestParam RoleType roleType) {
        log.info("User API: create user");
        User createdUser = userService.createUser(userDTO,tenantId, roleType);
        UserDTO createdUserDto = userMapper.toDto(createdUser);
        return new ResponseEntity<>(createdUserDto, HttpStatus.CREATED);
    }

    @PutMapping
    @Operation(summary = "Update user", description = "Update an existing user's details")
    public ResponseEntity<GenericResponseDTO<Boolean>> updateUser(@RequestBody UserDTO userDTO) {
        log.info("User API: updateEntity user");
        try {
            userService.updateUser(userDTO);
            GenericResponseDTO<Boolean> build = GenericResponseDTO.<Boolean>builder().response(true).build();
            return new ResponseEntity<>(build, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Failed to update user: {}", e.getMessage(), e);
            GenericResponseDTO<Boolean> build = GenericResponseDTO.<Boolean>builder().response(false).build();
            return new ResponseEntity<>(build, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update-password")
    @Operation(summary = "Update user password", description = "Update the password for an existing user")
    public ResponseEntity<GenericResponseDTO<Boolean>> updatePassword(@RequestBody UpdatePasswordRequestDTO updatePasswordRequest) {
        log.info("User API: processing password updateEntity for userId: ");
        GenericResponseDTO<Boolean> genericResponse = userService.updatePassword(updatePasswordRequest);
        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }

    @GetMapping("/me")
    @Operation(summary = "Get authenticated user", description = "Retrieve details of the currently authenticated user")
    public ResponseEntity<UserDTO> retrieveAuthenticatedUser() {
        Optional<UUID> currentUserId = AppSecurityUtils.getCurrentUserId();
        log.info("User API: retrieve authenticated user details for userId: ", currentUserId.get());
        UserDTO genericResponse = userService.getUserById(currentUserId.get());
        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }

    @GetMapping("/email-exists")
    @Operation(summary = "Check if email exists", description = "Check if a user with the specified email already exists")
    public ResponseEntity<GenericResponseDTO<Boolean>> exists(@RequestParam("email") String email) {
        GenericResponseDTO<Boolean> genericResponseDTO = userService.userEmailExists(email);
        return new ResponseEntity<>(genericResponseDTO, HttpStatus.OK);
    }

}
