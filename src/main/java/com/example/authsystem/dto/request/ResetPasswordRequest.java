package com.example.authsystem.dto.request;

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
public class ResetPasswordRequest {
    @NotBlank(message = "OTP cannot be blank")
    @Pattern(regexp = "^\\d{6}$", message = "OTP must be exactly 6 digits")
    String otp;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email format is invalid")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message = "Email must contain a valid domain (e.g. .com, .ru, .org)"
    )
    String email;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6, max = 150, message = "Password must be between 6 and 150 characters")
    String newPassword;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6, max = 150, message = "Password must be between 6 and 150 characters")
    String confirmPassword;
}