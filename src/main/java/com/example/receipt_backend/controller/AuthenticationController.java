package com.example.receipt_backend.controller;

import com.example.receipt_backend.dto.UserDTO;
import com.example.receipt_backend.dto.request.*;
import com.example.receipt_backend.dto.response.AuthResponseDTO;
import com.example.receipt_backend.dto.response.GenericResponseDTO;
import com.example.receipt_backend.service.AuthenticationService;
import com.example.receipt_backend.service.UserService;
import com.example.receipt_backend.utils.RoleType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "APIs for user authentication and account management")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final UserService userService;

    public AuthenticationController(AuthenticationService authenticationService,
                                    UserService userService) {
        this.authenticationService = authenticationService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> loginUser(@RequestBody LoginRequestDTO loginRequest) {
        AuthResponseDTO authResponseDTO = authenticationService.loginUser(loginRequest);
        return ResponseEntity.ok(authResponseDTO);
    }

    @PostMapping("/register")
    public ResponseEntity< GenericResponseDTO<Boolean>> registerUser(@Valid @RequestBody RegisterUserRequestDTO registerUserRequestDTO) {
        GenericResponseDTO<Boolean> userDTO = authenticationService.registerUser(registerUserRequestDTO);
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    @GetMapping("/resend-verification-email")
    public ResponseEntity<GenericResponseDTO<Boolean>> resendVerificationEmail(@RequestParam("email") String email) {
        log.info("Authentication API: resendVerificationEmail: ", email);
        GenericResponseDTO<Boolean> resendVerificationEmailStatus = userService.sendVerificationEmail(email);
        return new ResponseEntity<>(resendVerificationEmailStatus, HttpStatus.OK);
    }

    @PostMapping("/check-verification-code")
    public ResponseEntity<GenericResponseDTO<Boolean>> checkVerificationCode(@RequestBody VerifyEmailRequestDTO verifyEmailRequestDTO) {
        log.info("Authentication API: checkVerificationCode: ", verifyEmailRequestDTO.getEmail());
        GenericResponseDTO<Boolean> checkVerificationCodeStatus = userService.verifyEmailAddress(verifyEmailRequestDTO);
        return new ResponseEntity<>(checkVerificationCodeStatus, HttpStatus.OK);
    }

    @PostMapping("/send-forgot-password")
    public ResponseEntity<GenericResponseDTO<Boolean>> sendResetPasswordEmail(@RequestBody ForgotPasswordRequestDTO forgotPasswordRequestDTO) {
        log.info("Authentication API: sendResetPasswordEmail: ", forgotPasswordRequestDTO.getEmail());
        GenericResponseDTO<Boolean> resendVerificationEmailStatus = userService.sendResetPasswordEmail(forgotPasswordRequestDTO);
        return new ResponseEntity<>(resendVerificationEmailStatus, HttpStatus.OK);
    }

    @PostMapping("/process-password-reset")
    public ResponseEntity<GenericResponseDTO<Boolean>> verifyAndProcessPasswordResetRequest(@RequestBody ResetPasswordRequestDTO resetPasswordRequestDTO) {
        log.info("Authentication API: verifyAndProcessPasswordResetRequest: ", resetPasswordRequestDTO.getEmail());
        GenericResponseDTO<Boolean> checkVerificationCodeStatus = userService.verifyAndProcessPasswordResetRequest(resetPasswordRequestDTO);
        return new ResponseEntity<>(checkVerificationCodeStatus, HttpStatus.OK);
    }
}