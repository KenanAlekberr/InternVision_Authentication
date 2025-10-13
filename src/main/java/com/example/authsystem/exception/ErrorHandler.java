package com.example.authsystem.exception;

import com.example.authsystem.exception.custom.AlreadyExistException;
import com.example.authsystem.exception.custom.ConfirmPasswordException;
import com.example.authsystem.exception.custom.IncorrectOldPasswordException;
import com.example.authsystem.exception.custom.InvalidCredentialsException;
import com.example.authsystem.exception.custom.NotFoundException;
import com.example.authsystem.exception.custom.TokenException;
import com.example.authsystem.exception.custom.ValidatorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

import static com.example.authsystem.exception.ExceptionConstants.ALREADY_EXCEPTION;
import static com.example.authsystem.exception.ExceptionConstants.CONFIRM_PASSWORD;
import static com.example.authsystem.exception.ExceptionConstants.HTTP_METHOD_IS_NOT_CORRECT;
import static com.example.authsystem.exception.ExceptionConstants.INCORRECT_OLD_PASSWORD;
import static com.example.authsystem.exception.ExceptionConstants.INVALID_CREDENTIALS;
import static com.example.authsystem.exception.ExceptionConstants.TOKEN_EXCEPTION;
import static com.example.authsystem.exception.ExceptionConstants.UNEXPECTED_EXCEPTION;
import static com.example.authsystem.exception.ExceptionConstants.VALIDATION_EXCEPTION;
import static org.springframework.http.HttpStatus.ALREADY_REPORTED;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler(Exception.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ErrorResponse handle(Exception exception) {
        log.error("Exception, ", exception);
        return ErrorResponse.builder()
                .code(UNEXPECTED_EXCEPTION.getCode())
                .message(UNEXPECTED_EXCEPTION.getMessage())
                .build();
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(METHOD_NOT_ALLOWED)
    public ErrorResponse handle(HttpRequestMethodNotSupportedException exception) {
        log.error("HttpRequestMethodNotSupportedException, ", exception);
        return ErrorResponse.builder()
                .code(HTTP_METHOD_IS_NOT_CORRECT.getCode())
                .message(HTTP_METHOD_IS_NOT_CORRECT.getMessage())
                .build();
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    public ErrorResponse handle(NotFoundException exception) {
        log.error("NotFoundException, ", exception);
        return ErrorResponse.builder()
                .code(exception.getCode())
                .message(exception.getMessage())
                .build();
    }

    @ExceptionHandler(AlreadyExistException.class)
    @ResponseStatus(ALREADY_REPORTED)
    public ErrorResponse handle(AlreadyExistException exception) {
        log.error("AlreadyExistException, ", exception);
        return ErrorResponse.builder()
                .code(ALREADY_EXCEPTION.getCode())
                .message(ALREADY_EXCEPTION.getMessage())
                .build();
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    @ResponseStatus(UNAUTHORIZED)
    public ErrorResponse handle(InvalidCredentialsException exception) {
        log.error("InvalidCredentialsException, ", exception);
        return ErrorResponse.builder()
                .code(INVALID_CREDENTIALS.getCode())
                .message(INVALID_CREDENTIALS.getMessage())
                .build();
    }

    @ExceptionHandler(IncorrectOldPasswordException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handle(IncorrectOldPasswordException exception) {
        log.error("IncorrectOldPasswordException, ", exception);
        return ErrorResponse.builder()
                .code(INCORRECT_OLD_PASSWORD.getCode())
                .message(INCORRECT_OLD_PASSWORD.getMessage())
                .build();
    }

    @ExceptionHandler(ConfirmPasswordException.class)
    @ResponseStatus(CONFLICT)
    public ErrorResponse handle(ConfirmPasswordException exception) {
        log.error("ConfirmPasswordException, ", exception);
        return ErrorResponse.builder()
                .code(CONFIRM_PASSWORD.getCode())
                .message(CONFIRM_PASSWORD.getMessage())
                .build();
    }

    @ExceptionHandler(TokenException.class)
    @ResponseStatus(CONFLICT)
    public ErrorResponse handle(TokenException exception) {
        log.error("TokenException, ", exception);
        return ErrorResponse.builder()
                .code(TOKEN_EXCEPTION.getCode())
                .message(TOKEN_EXCEPTION.getMessage())
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handle(MethodArgumentNotValidException exception) {
        log.error("MethodArgumentNotValidException, ", exception);
        List<ValidatorException> validationErrors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> ValidatorException.builder()
                        .field(error.getField())
                        .message(error.getDefaultMessage())
                        .build())
                .toList();

        return ErrorResponse.builder()
                .code(VALIDATION_EXCEPTION.getCode())
                .message(VALIDATION_EXCEPTION.getMessage())
                .validationExceptions(validationErrors)
                .build();
    }
}