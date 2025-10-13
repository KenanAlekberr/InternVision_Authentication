package com.example.authsystem.service.impl;

import com.example.authsystem.dto.request.UpdateUserRequest;
import com.example.authsystem.dto.response.UserResponse;
import com.example.authsystem.entity.UserEntity;
import com.example.authsystem.exception.custom.NotFoundException;
import com.example.authsystem.repository.UserRepository;
import com.example.authsystem.service.abstraction.UserService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.authsystem.enums.UserStatus.DELETED;
import static com.example.authsystem.exception.ExceptionConstants.USER_NOT_FOUND;
import static com.example.authsystem.mapper.UserMapper.USER_MAPPER;
import static lombok.AccessLevel.PRIVATE;

@Service
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    UserRepository userRepository;

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(USER_MAPPER::buildUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse getUserById(Long id) {
        UserEntity userEntity = fetchUserIfExist(id);

        return USER_MAPPER.buildUserResponse(userEntity);
    }

    @Override
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        UserEntity userEntity = fetchUserIfExist(id);

        USER_MAPPER.updateUser(userEntity, request);
        userRepository.save(userEntity);

        return USER_MAPPER.buildUserResponse(userEntity);
    }

    @Override
    public void deleteUser(Long id) {
        UserEntity userEntity = fetchUserIfExist(id);
        userEntity.setUserStatus(DELETED);

        userRepository.save(userEntity);
    }

    private UserEntity fetchUserIfExist(Long id) {
        return userRepository.findById(id).orElseThrow
                (() -> new NotFoundException(USER_NOT_FOUND.getCode(), USER_NOT_FOUND.getMessage()));
    }
}