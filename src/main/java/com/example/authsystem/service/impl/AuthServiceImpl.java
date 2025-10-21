package com.example.authsystem.service.impl;

import com.example.authsystem.dto.request.ChangePasswordRequest;
import com.example.authsystem.dto.request.LoginRequest;
import com.example.authsystem.dto.request.ResetPasswordRequest;
import com.example.authsystem.dto.request.UserRegisterRequest;
import com.example.authsystem.dto.request.VerifyOtpRequest;
import com.example.authsystem.dto.response.AuthResponse;
import com.example.authsystem.dto.response.UserResponse;
import com.example.authsystem.entity.UserEntity;
import com.example.authsystem.exception.custom.AlreadyExistException;
import com.example.authsystem.exception.custom.AuthenticationServiceException;
import com.example.authsystem.exception.custom.ConfirmPasswordException;
import com.example.authsystem.exception.custom.InvalidCredentialsException;
import com.example.authsystem.exception.custom.InvalidOtpException;
import com.example.authsystem.exception.custom.InvalidStateException;
import com.example.authsystem.exception.custom.NotFoundException;
import com.example.authsystem.repository.UserRepository;
import com.example.authsystem.security.CustomUserDetails;
import com.example.authsystem.service.abstraction.AuthService;
import com.example.authsystem.service.abstraction.EmailService;
import com.example.authsystem.util.CacheUtilWithRedisson;
import com.example.authsystem.util.JWTUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import static com.example.authsystem.constant.AppConstants.ACCESS_BLACKLIST_PREFIX;
import static com.example.authsystem.constant.AppConstants.OTP_KEY_PREFIX;
import static com.example.authsystem.constant.AppConstants.REFRESH_KEY_PREFIX;
import static com.example.authsystem.constant.AppConstants.USER_KEY_PREFIX;
import static com.example.authsystem.exception.ExceptionConstants.ALREADY_EXCEPTION;
import static com.example.authsystem.exception.ExceptionConstants.AUTHENTICATION_SERVICE_EXCEPTION;
import static com.example.authsystem.exception.ExceptionConstants.CONFIRM_PASSWORD;
import static com.example.authsystem.exception.ExceptionConstants.INVALID_CREDENTIALS;
import static com.example.authsystem.exception.ExceptionConstants.INVALID_OTP_EXCEPTION;
import static com.example.authsystem.exception.ExceptionConstants.INVALID_STATE_EXCEPTION;
import static com.example.authsystem.exception.ExceptionConstants.USER_NOT_FOUND;
import static com.example.authsystem.mapper.UserMapper.USER_MAPPER;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static lombok.AccessLevel.PRIVATE;

@Service
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    AuthenticationManager authenticationManager;
    JWTUtil jwtUtil;
    EmailService emailService;
    CacheUtilWithRedisson cache;

    @Override
    public String register(UserRegisterRequest request) {
        try {
            userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
                throw new AlreadyExistException(ALREADY_EXCEPTION.getCode(), ALREADY_EXCEPTION.getMessage());
            });

            if (!request.getPassword().equals(request.getConfirmPassword()))
                throw new ConfirmPasswordException(CONFIRM_PASSWORD.getCode(), CONFIRM_PASSWORD.getMessage());

            String otp = String.valueOf(new Random().nextInt(900000) + 100000);

            cache.set(USER_KEY_PREFIX + request.getEmail(), request, 5, MINUTES);
            cache.set(OTP_KEY_PREFIX + request.getEmail(), otp, 5, MINUTES);

            emailService.sendEmail(request.getEmail(), "Your OTP Code", otp);

            return "OTP has been sent to your email";
        } catch (AlreadyExistException e) {
            throw new AlreadyExistException(ALREADY_EXCEPTION.getCode(), ALREADY_EXCEPTION.getMessage());
        }

    }

    @Override
    public UserResponse verifyOtp(VerifyOtpRequest request) {
        String email = request.getEmail();
        String storedOtp = cache.get(OTP_KEY_PREFIX + email, String.class);

        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            throw new AlreadyExistException(ALREADY_EXCEPTION.getCode(), ALREADY_EXCEPTION.getMessage());
        });

        if (storedOtp == null || !storedOtp.equals(request.getOtp()))
            throw new InvalidOtpException(INVALID_CREDENTIALS.getCode(), INVALID_CREDENTIALS.getMessage());

        UserRegisterRequest pendingUser = cache.get(USER_KEY_PREFIX + email, UserRegisterRequest.class);

        if (pendingUser == null)
            throw new InvalidStateException(INVALID_STATE_EXCEPTION.getCode(), INVALID_STATE_EXCEPTION.getMessage());

        UserEntity user = USER_MAPPER.buildUserEntity(pendingUser);

        user.setPassword(passwordEncoder.encode(pendingUser.getPassword()));
        user.setConfirmPassword(passwordEncoder.encode(pendingUser.getPassword()));

        userRepository.save(user);

        cache.delete(OTP_KEY_PREFIX + email);
        cache.delete(USER_KEY_PREFIX + email);

        log.info("User verified and saved successfully: " + email);

        return USER_MAPPER.buildUserResponse(user);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            Map<String, Object> claims = Map.of("userId", userDetails.getId(),
                    "roles", userDetails.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.toList()));

            String accessToken = jwtUtil.generateAccessToken(userDetails.getUsername(), claims);
            String refreshToken = jwtUtil.generateRefreshToken(userDetails.getUsername());

            cache.set(REFRESH_KEY_PREFIX + userDetails.getUsername(),
                    refreshToken, 6, MINUTES);

            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .user(USER_MAPPER.buildUserResponse(userDetails.user()))
                    .build();
        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException(INVALID_CREDENTIALS.getCode(),
                    INVALID_CREDENTIALS.getMessage());
        } catch (InternalAuthenticationServiceException e) {
            throw new AuthenticationServiceException(AUTHENTICATION_SERVICE_EXCEPTION.getCode(),
                    AUTHENTICATION_SERVICE_EXCEPTION.getMessage());
        }
    }

    @Override
    public void changePassword(Long id, ChangePasswordRequest request) {
        UserEntity user = userRepository.findById(id).orElseThrow(
                () -> new NotFoundException(USER_NOT_FOUND.getCode(), USER_NOT_FOUND.getMessage()));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword()))
            throw new InvalidCredentialsException(INVALID_CREDENTIALS.getCode(), INVALID_CREDENTIALS.getMessage());

        if (!request.getNewPassword().equals(request.getConfirmPassword()))
            throw new ConfirmPasswordException(CONFIRM_PASSWORD.getCode(), CONFIRM_PASSWORD.getMessage());

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        cache.delete(REFRESH_KEY_PREFIX + user.getEmail());
    }

    @Override
    public void logout(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer "))
            return;

        String accessToken = authHeader.substring(7);
        String username = jwtUtil.extractUsername(accessToken);

        if (username != null) {
            cache.delete(REFRESH_KEY_PREFIX + username);

            long ttlMillis = computeTokenTtlMillis(accessToken);

            if (ttlMillis > 0)
                cache.set(ACCESS_BLACKLIST_PREFIX + accessToken, "blacklisted", (int) MILLISECONDS.toMinutes(ttlMillis), MINUTES);
        }
    }

    @Override
    public void forgotPassword(String email) {
        try {
            userRepository.findByEmail(email).orElseThrow(
                    () -> new NotFoundException(USER_NOT_FOUND.getCode(), USER_NOT_FOUND.getMessage()));

            String otp = String.format("%06d", new Random().nextInt(999999));

            cache.set(OTP_KEY_PREFIX + email, otp, 10, MINUTES);

            emailService.sendEmail(email, "Your OTP Code", otp);
        } catch (RuntimeException e) {
            throw new NotFoundException(USER_NOT_FOUND.getCode(), "User not found or invalid credentials");
        }
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        String cachedOtp = cache.get(OTP_KEY_PREFIX + request.getEmail(), String.class);

        if (cachedOtp == null || !cachedOtp.equals(request.getOtp()))
            throw new InvalidOtpException(INVALID_OTP_EXCEPTION.getCode(), INVALID_OTP_EXCEPTION.getMessage());

        if (!request.getNewPassword().equals(request.getConfirmPassword()))
            throw new ConfirmPasswordException(CONFIRM_PASSWORD.getCode(), CONFIRM_PASSWORD.getMessage());

        UserEntity user = userRepository.findByEmail(request.getEmail()).orElseThrow(
                () -> new NotFoundException(USER_NOT_FOUND.getCode(), USER_NOT_FOUND.getMessage()));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        cache.delete(OTP_KEY_PREFIX + request.getEmail());
    }

    private long computeTokenTtlMillis(String token) {
        try {
            Claims claims = jwtUtil.extractAllClaims(token);
            if (claims == null || claims.getExpiration() == null) return -1;
            return claims.getExpiration().getTime() - System.currentTimeMillis();
        } catch (Exception e) {
            return -1;
        }
    }
}