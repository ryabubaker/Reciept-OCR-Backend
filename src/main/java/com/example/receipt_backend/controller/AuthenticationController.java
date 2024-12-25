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
    @Operation(summary = "Login user", description = "Authenticate a user and return a token")
    public ResponseEntity<AuthResponseDTO> loginUser(@RequestBody LoginRequestDTO loginRequest) {
        AuthResponseDTO authResponseDTO = authenticationService.loginUser(loginRequest);
        return ResponseEntity.ok(authResponseDTO);
    }

    @PostMapping("/register")
    @Operation(summary = "Register user", description = "Register a new user in the system")
    public GenericResponseDTO<String> registerUser(@Valid @RequestBody RegisterUserRequestDTO registerUserRequestDTO) {
        UserDTO userDTO = authenticationService.registerUser(registerUserRequestDTO);
        return new GenericResponseDTO<>("Registered Successfully", "200");
    }

    @GetMapping("/resend-verification-email")
    @Operation(summary = "Resend verification email", description = "Resend the verification email to the user")
    public ResponseEntity<?> resendVerificationEmail(@RequestParam("email") String email) {
        log.info("Authentication API: resendVerificationEmail: ", email);
        GenericResponseDTO<Boolean> resendVerificationEmailStatus = userService.sendVerificationEmail(email);
        return new ResponseEntity<>(resendVerificationEmailStatus, HttpStatus.OK);
    }

    @PostMapping("/check-verification-code")
    @Operation(summary = "Check verification code", description = "Check the verification code sent to the user's email")
    public ResponseEntity<?> checkVerificationCode(@RequestBody VerifyEmailRequestDTO verifyEmailRequestDTO) {
        log.info("Authentication API: checkVerificationCode: ", verifyEmailRequestDTO.getEmail());
        GenericResponseDTO<Boolean> checkVerificationCodeStatus = userService.verifyEmailAddress(verifyEmailRequestDTO);
        return new ResponseEntity<>(checkVerificationCodeStatus, HttpStatus.OK);
    }

    @PostMapping("/send-forgot-password")
    @Operation(summary = "Send forgot password email", description = "Send an email to reset the user's password")
    public ResponseEntity<?> sendResetPasswordEmail(@RequestBody ForgotPasswordRequestDTO forgotPasswordRequestDTO) {
        log.info("Authentication API: sendResetPasswordEmail: ", forgotPasswordRequestDTO.getEmail());
        GenericResponseDTO<Boolean> resendVerificationEmailStatus = userService.sendResetPasswordEmail(forgotPasswordRequestDTO);
        return new ResponseEntity<>(resendVerificationEmailStatus, HttpStatus.OK);
    }

    @PostMapping("/process-password-reset")
    @Operation(summary = "Process password reset", description = "Verify and process the password reset request")
    public ResponseEntity<?> verifyAndProcessPasswordResetRequest(@RequestBody ResetPasswordRequestDTO resetPasswordRequestDTO) {
        log.info("Authentication API: verifyAndProcessPasswordResetRequest: ", resetPasswordRequestDTO.getEmail());
        GenericResponseDTO<Boolean> checkVerificationCodeStatus = userService.verifyAndProcessPasswordResetRequest(resetPasswordRequestDTO);
        return new ResponseEntity<>(checkVerificationCodeStatus, HttpStatus.OK);
    }
}