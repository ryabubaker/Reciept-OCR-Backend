package com.example.receipt_backend.service;

import com.example.receipt_backend.dto.request.LoginRequestDTO;
import com.example.receipt_backend.dto.request.RegisterUserRequestDTO;
import com.example.receipt_backend.dto.response.AuthResponseDTO;
import com.example.receipt_backend.dto.response.GenericResponseDTO;

public interface AuthenticationService {

    AuthResponseDTO loginUser(LoginRequestDTO loginRequest);

    GenericResponseDTO<Boolean> registerUser(RegisterUserRequestDTO registerUserRequestDTO);

}
