package com.example.authsystem.mapper;

import com.example.authsystem.dto.request.user.UpdateUserRequest;
import com.example.authsystem.dto.request.user.UserRegisterRequest;
import com.example.authsystem.dto.response.UserResponse;
import com.example.authsystem.entity.UserEntity;
import io.micrometer.common.util.StringUtils;

import java.time.LocalDateTime;

import static com.example.authsystem.enums.UserRole.USER;
import static com.example.authsystem.enums.UserStatus.ACTIVE;
import static com.example.authsystem.enums.UserStatus.IN_PROGRESS;

public enum UserMapper {
    USER_MAPPER;

    public UserEntity buildUserEntity(UserRegisterRequest request) {
        return UserEntity.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(request.getPassword())
                .confirmPassword(request.getConfirmPassword())
                .userStatus(ACTIVE)
                .userRole(USER)
                .build();
    }

    public UserResponse buildUserResponse(UserEntity user) {
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .userStatus(user.getUserStatus())
                .userRole(user.getUserRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public void updateUser(UserEntity user, UpdateUserRequest request) {
        if (StringUtils.isNotEmpty(request.getFirstName()))
            user.setFirstName(request.getFirstName());

        if (StringUtils.isNotEmpty(request.getLastName()))
            user.setLastName(request.getLastName());

        if (StringUtils.isNotEmpty(request.getEmail()))
            user.setEmail(request.getEmail());

        user.setUserStatus(IN_PROGRESS);
        user.setUpdatedAt(LocalDateTime.now());
    }
}