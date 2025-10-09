package com.example.authsystem.dto.response;

import com.example.authsystem.enums.UserRole;
import com.example.authsystem.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = PRIVATE)
@Builder
public class UserResponse {
    Long id;
    String firstName;
    String lastName;
    String email;
    UserStatus userStatus;
    UserRole userRole;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}