package com.example.authsystem.controller;

import com.example.authsystem.dto.request.auth.ChangePasswordRequest;
import com.example.authsystem.dto.request.auth.LoginRequest;
import com.example.authsystem.dto.request.user.UserRegisterRequest;
import com.example.authsystem.dto.response.UserResponse;
import com.example.authsystem.service.abstraction.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static lombok.AccessLevel.PRIVATE;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/v1/auth")
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AuthController {
    AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(CREATED)
    public UserResponse register(@Valid @RequestBody UserRegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    @ResponseStatus(OK)
    public UserResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/changePassword/{id}")
    @ResponseStatus(OK)
    public void changePassword(@PathVariable Long id, @Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(id, request);
    }
}