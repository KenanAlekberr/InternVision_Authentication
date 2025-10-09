package com.example.authsystem.dto.request.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = PRIVATE)
public class UserRegisterRequest {
    @NotBlank(message = "First name cannot be blank")
    @Size(min = 2, max = 30, message = "First name must be between 2 and 30 characters")
    String firstName;

    @NotBlank(message = "Last name cannot be blank")
    @Size(min = 2, max = 30, message = "Last name must be between 2 and 30 characters")
    String lastName;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email should be valid")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message = "Email must contain a valid domain (e.g. .com, .ru, .org)"
    )
    @Size(min = 15, max = 100, message = "Email must be must be between 2 and 100 characters")
    String email;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6, max = 30, message = "Password must be between 6 and 30 characters")
    String password;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6, max = 30, message = "Password must be between 6 and 30 characters")
    String confirmPassword;
}