package com.example.authsystem.service.abstraction;

import com.example.authsystem.dto.request.user.UpdateUserRequest;
import com.example.authsystem.dto.response.UserResponse;

import java.util.List;

public interface UserService {
    List<UserResponse> getAllUsers();

    UserResponse getUserById(Long id);

    UserResponse updateUser(Long id, UpdateUserRequest request);

    void deleteUser(Long id);
}