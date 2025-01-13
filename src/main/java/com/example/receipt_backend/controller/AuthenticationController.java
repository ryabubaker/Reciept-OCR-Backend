package com.example.receipt_backend.controller;

import com.example.receipt_backend.dto.request.*;
import com.example.receipt_backend.dto.response.AuthResponseDTO;
import com.example.receipt_backend.dto.response.GenericResponseDTO;
import com.example.receipt_backend.service.AuthenticationService;
import com.example.receipt_backend.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "APIs for user authentication and account management")
public class AuthenticationController {

    private final AuthenticationService authService;
    private final UserService userService;


    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> loginUser(@RequestBody LoginRequestDTO loginRequest) {
        AuthResponseDTO authResponseDTO = authService.loginUser(loginRequest);
        return ResponseEntity.ok(authResponseDTO);
    }

//    @PostMapping("/create-user")
//    public ResponseEntity< GenericResponseDTO<Boolean>> registerUser(@Valid @RequestBody RegisterUserRequestDTO registerUserRequestDTO) {
//        GenericResponseDTO<Boolean> userDTO = authService.registerUser(registerUserRequestDTO);
//        return new ResponseEntity<>(userDTO, HttpStatus.OK);
//    }

    @PostMapping("/register")
    public ResponseEntity<GenericResponseDTO<String>> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        GenericResponseDTO<String> response = authService.registerUserWithInvitation(registerRequest);
        return  ResponseEntity.ok(response);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponseDTO> refreshAccessToken(@RequestBody RefreshTokenRequestDTO refreshTokenRequest) {
        AuthResponseDTO authResponseDTO = authService.refresh(refreshTokenRequest.getRefreshToken());
        return ResponseEntity.ok(authResponseDTO);
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