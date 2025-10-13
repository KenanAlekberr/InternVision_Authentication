package com.example.authsystem.service.abstraction;

import com.example.authsystem.dto.request.ChangePasswordRequest;
import com.example.authsystem.dto.request.LoginRequest;
import com.example.authsystem.dto.request.ResetPasswordRequest;
import com.example.authsystem.dto.request.UserRegisterRequest;
import com.example.authsystem.dto.response.AuthResponse;
import com.example.authsystem.dto.response.UserResponse;

public interface AuthService {
    UserResponse register(UserRegisterRequest request);

    AuthResponse login(LoginRequest request);

    void changePassword(Long id, ChangePasswordRequest request);

    void logout(String authHeader);

    void forgotPassword(String email);

    void resetPassword(ResetPasswordRequest request);
}