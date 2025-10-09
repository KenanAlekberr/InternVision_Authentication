package com.example.authsystem.service.impl;

import com.example.authsystem.dto.request.user.UpdateUserRequest;
import com.example.authsystem.dto.response.UserResponse;
import com.example.authsystem.service.abstraction.UserService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Service
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    @Override
    public List<UserResponse> getAllUsers() {
        return List.of();
    }

    @Override
    public UserResponse getUserById(Long id) {
        return null;
    }

    @Override
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        return null;
    }

    @Override
    public void deleteUser(Long id) {

    }
}