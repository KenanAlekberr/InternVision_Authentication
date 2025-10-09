package com.example.authsystem.service.abstraction;

import com.example.authsystem.dto.request.auth.ChangePasswordRequest;
import com.example.authsystem.dto.request.auth.LoginRequest;
import com.example.authsystem.dto.request.user.UserRegisterRequest;
import com.example.authsystem.dto.response.UserResponse;

public interface AuthService {
    UserResponse register(UserRegisterRequest request);

    UserResponse login(LoginRequest request);

    void changePassword(Long id, ChangePasswordRequest request);
}