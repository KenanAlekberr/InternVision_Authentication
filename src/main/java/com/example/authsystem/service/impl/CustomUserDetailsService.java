package com.example.authsystem.service.impl;

import com.example.authsystem.entity.UserEntity;
import com.example.authsystem.exception.custom.NotFoundException;
import com.example.authsystem.repository.UserRepository;
import com.example.authsystem.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import static com.example.authsystem.exception.ExceptionConstants.USER_NOT_FOUND;
import static lombok.AccessLevel.PRIVATE;


@Service
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    UserRepository userRepository;

    @Override
    public CustomUserDetails loadUserByUsername(String email) throws NotFoundException {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND.getCode(), USER_NOT_FOUND.getMessage()));

        return new CustomUserDetails(user);
    }
}