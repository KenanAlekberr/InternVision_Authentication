package com.example.authsystem.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public enum ExceptionConstants {
    UNEXPECTED_EXCEPTION("UNEXPECTED_EXCEPTION", "Unexpected exception occurred"),
    HTTP_METHOD_IS_NOT_CORRECT("HTTP_METHOD_IS_NOT_CORRECT", "http method is not correct"),
    USER_NOT_FOUND("USER_NOT_FOUND", "User not found by id"),
    ALREADY_EXCEPTION("ALREADY_EXCEPTION", "Email already exists"),
    INVALID_CREDENTIALS("INVALID_CREDENTIALS", "Invalid email or password"),
    INCORRECT_OLD_PASSWORD("INCORRECT_OLD_PASSWORD", "Old password is incorrect"),
    CONFIRM_PASSWORD("CONFIRM_PASSWORD", "Password and confirm password must match"),
    TOKEN_EXCEPTION("TOKEN_EXCEPTION", "Reset token invalid or expired"),
    INVALID_OTP_EXCEPTION("INVALID_OTP_EXCEPTION", "Invalid or expired OTP code"),
    INVALID_STATE_EXCEPTION("INVALID_STATE_EXCEPTION", "No pending registration found or expired"),
    VALIDATION_EXCEPTION("VALIDATION_EXCEPTION", "Validation exception");

    String code;
    String message;
}