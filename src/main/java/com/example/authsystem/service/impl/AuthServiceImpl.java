package com.example.authsystem.service.impl;

import com.example.authsystem.dto.request.ChangePasswordRequest;
import com.example.authsystem.dto.request.LoginRequest;
import com.example.authsystem.dto.request.ResetPasswordRequest;
import com.example.authsystem.dto.request.UserRegisterRequest;
import com.example.authsystem.dto.response.AuthResponse;
import com.example.authsystem.dto.response.UserResponse;
import com.example.authsystem.entity.UserEntity;
import com.example.authsystem.exception.custom.AlreadyExistException;
import com.example.authsystem.exception.custom.ConfirmPasswordException;
import com.example.authsystem.exception.custom.IncorrectOldPasswordException;
import com.example.authsystem.exception.custom.NotFoundException;
import com.example.authsystem.repository.UserRepository;
import com.example.authsystem.security.JwtBlacklistService;
import com.example.authsystem.security.JwtUtil;
import com.example.authsystem.service.abstraction.AuthService;
import com.example.authsystem.service.abstraction.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static com.example.authsystem.exception.ExceptionConstants.ALREADY_EXCEPTION;
import static com.example.authsystem.exception.ExceptionConstants.CONFIRM_PASSWORD;
import static com.example.authsystem.exception.ExceptionConstants.INCORRECT_OLD_PASSWORD;
import static com.example.authsystem.exception.ExceptionConstants.USER_NOT_FOUND;
import static com.example.authsystem.mapper.UserMapper.USER_MAPPER;
import static lombok.AccessLevel.PRIVATE;

@Service
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    AuthenticationManager authenticationManager;
    JwtUtil jwtUtil;
    JwtBlacklistService jwtBlacklistService;
    EmailService emailService;

    static final Map<String, ResetToken> resetTokens = new ConcurrentHashMap<>();
    static final long RESET_TOKEN_EXPIRY_MS = 15 * 60 * 1000;

    @Override
    public UserResponse register(UserRegisterRequest request) {
        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            throw new AlreadyExistException(ALREADY_EXCEPTION.getCode(), ALREADY_EXCEPTION.getMessage());
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
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        UserEntity user = userRepository.findByEmail(request.getEmail()).orElseThrow(() ->
                new NotFoundException(USER_NOT_FOUND.getCode(), USER_NOT_FOUND.getMessage()));

        String token = jwtUtil.generateToken(user.getEmail(), user.getId());
        return AuthResponse.builder()
                .token(token)
                .user(USER_MAPPER.buildUserResponse(user))
                .build();
    }

    @Override
    public void changePassword(Long id, ChangePasswordRequest request) {
        UserEntity user = userRepository.findById(id).orElseThrow(() ->
                new NotFoundException(USER_NOT_FOUND.getCode(), USER_NOT_FOUND.getMessage()));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword()))
            throw new IncorrectOldPasswordException(INCORRECT_OLD_PASSWORD.getCode(), INCORRECT_OLD_PASSWORD.getMessage());

        if (!request.getNewPassword().equals(request.getConfirmPassword()))
            throw new ConfirmPasswordException(CONFIRM_PASSWORD.getCode(), CONFIRM_PASSWORD.getMessage());

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public void logout(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return;

        String token = authHeader.substring(7);
        var claims = jwtUtil.getAllClaimsFromToken(token);
        long exp = claims.getExpiration().getTime();

        jwtBlacklistService.blacklist(token, exp);
    }

    @Override
    public void forgotPassword(String email) {
        UserEntity user = userRepository.findByEmail(email).orElseThrow(() ->
                new NotFoundException(USER_NOT_FOUND.getCode(), USER_NOT_FOUND.getMessage()));

        String token = UUID.randomUUID().toString();
        long expiry = Instant.now().toEpochMilli() + RESET_TOKEN_EXPIRY_MS;
        resetTokens.put(token, new ResetToken(user.getId(), expiry));

        String resetLink = "http://localhost:8081/api/v1/auth/reset-password?token=" + token;
        emailService.sendEmail(
                user.getEmail(),
                "Password Reset Request",
                "Hello " + user.getFirstName() + ",\n\n" +
                        "You requested to reset your password. Click the link below to set a new one:\n" +
                        resetLink + "\n\n" +
                        "This link will expire in 15 minutes.\n\n" +
                        "If you didn’t request this, just ignore this email."
        );
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        ResetToken resetToken = resetTokens.get(request.getToken());

        if (resetToken == null || Instant.now().toEpochMilli() > resetToken.expiry)
            throw new RuntimeException("Reset token invalid or expired");

        if (!request.getNewPassword().equals(request.getConfirmPassword()))
            throw new ConfirmPasswordException(CONFIRM_PASSWORD.getCode(), CONFIRM_PASSWORD.getMessage());

        UserEntity user = userRepository.findById(resetToken.userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND.getCode(), USER_NOT_FOUND.getMessage()));
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(user);
        resetTokens.remove(request.getToken());
    }

    private static class ResetToken {
        final Long userId;
        final long expiry;

        ResetToken(Long userId, long expiry) {
            this.userId = userId;
            this.expiry = expiry;
        }
    }
}