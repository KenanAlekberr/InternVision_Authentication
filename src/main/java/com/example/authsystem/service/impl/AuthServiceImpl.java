package com.example.authsystem.service.impl;

import com.example.authsystem.dto.request.auth.ChangePasswordRequest;
import com.example.authsystem.dto.request.auth.LoginRequest;
import com.example.authsystem.dto.request.user.UserRegisterRequest;
import com.example.authsystem.dto.response.UserResponse;
import com.example.authsystem.entity.UserEntity;
import com.example.authsystem.exception.custom.ConfirmPasswordException;
import com.example.authsystem.exception.custom.CustomAlreadyExistException;
import com.example.authsystem.exception.custom.CustomNotFoundException;
import com.example.authsystem.exception.custom.IncorrectOldPasswordException;
import com.example.authsystem.exception.custom.InvalidCredentialsException;
import com.example.authsystem.repository.UserRepository;
import com.example.authsystem.service.abstraction.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.example.authsystem.exception.ExceptionConstants.ALREADY_EXCEPTION;
import static com.example.authsystem.exception.ExceptionConstants.CONFIRM_PASSWORD;
import static com.example.authsystem.exception.ExceptionConstants.INCORRECT_OLD_PASSWORD;
import static com.example.authsystem.exception.ExceptionConstants.INVALID_CREDENTIALS;
import static com.example.authsystem.exception.ExceptionConstants.USER_NOT_FOUND;
import static com.example.authsystem.mapper.UserMapper.USER_MAPPER;
import static lombok.AccessLevel.PRIVATE;

@Service
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;

    @Override
    public UserResponse register(UserRegisterRequest request) {
        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            throw new CustomAlreadyExistException(ALREADY_EXCEPTION.getCode(), ALREADY_EXCEPTION.getMessage());
        });

        if (!request.getPassword().equals(request.getConfirmPassword()))
            throw new ConfirmPasswordException(CONFIRM_PASSWORD.getCode(), CONFIRM_PASSWORD.getMessage());

        UserEntity user = USER_MAPPER.buildUserEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setConfirmPassword(null);

        userRepository.save(user);

        return USER_MAPPER.buildUserResponse(user);
    }

    @Override
    public UserResponse login(LoginRequest request) {
        UserEntity user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new CustomNotFoundException(USER_NOT_FOUND.getCode(), USER_NOT_FOUND.getMessage()));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword()))
            throw new InvalidCredentialsException(INVALID_CREDENTIALS.getCode(), INVALID_CREDENTIALS.getMessage());

        return USER_MAPPER.buildUserResponse(user);
    }

    @Override
    public void changePassword(Long id, ChangePasswordRequest request) {
        UserEntity user = userRepository.findById(id).orElseThrow(() -> new CustomNotFoundException(USER_NOT_FOUND.getCode(), USER_NOT_FOUND.getMessage()));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword()))
            throw new IncorrectOldPasswordException(INCORRECT_OLD_PASSWORD.getCode(), INCORRECT_OLD_PASSWORD.getMessage());

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}