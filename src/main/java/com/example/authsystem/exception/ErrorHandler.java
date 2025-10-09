package com.example.authsystem.exception;

import com.example.authsystem.exception.custom.ConfirmPasswordException;
import com.example.authsystem.exception.custom.CustomAlreadyExistException;
import com.example.authsystem.exception.custom.CustomNotFoundException;
import com.example.authsystem.exception.custom.IncorrectOldPasswordException;
import com.example.authsystem.exception.custom.InvalidCredentialsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.example.authsystem.exception.ExceptionConstants.ALREADY_EXCEPTION;
import static com.example.authsystem.exception.ExceptionConstants.CONFIRM_PASSWORD;
import static com.example.authsystem.exception.ExceptionConstants.HTTP_METHOD_IS_NOT_CORRECT;
import static com.example.authsystem.exception.ExceptionConstants.INCORRECT_OLD_PASSWORD;
import static com.example.authsystem.exception.ExceptionConstants.INVALID_CREDENTIALS;
import static com.example.authsystem.exception.ExceptionConstants.UNEXPECTED_EXCEPTION;
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
        return new ErrorResponse(UNEXPECTED_EXCEPTION.getCode(), UNEXPECTED_EXCEPTION.getMessage());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(METHOD_NOT_ALLOWED)
    public ErrorResponse handle(HttpRequestMethodNotSupportedException exception) {
        log.error("HttpRequestMethodNotSupportedException, ", exception);
        return new ErrorResponse(HTTP_METHOD_IS_NOT_CORRECT.getCode(), HTTP_METHOD_IS_NOT_CORRECT.getMessage());
    }

    @ExceptionHandler(CustomNotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    public ErrorResponse handle(CustomNotFoundException exception) {
        log.error("NotFoundException, ", exception);
        return new ErrorResponse(exception.getCode(), exception.getMessage());
    }

    @ExceptionHandler(CustomAlreadyExistException.class)
    @ResponseStatus(ALREADY_REPORTED)
    public ErrorResponse handle(CustomAlreadyExistException exception) {
        log.error("AlreadyExistException, ", exception);
        return new ErrorResponse(ALREADY_EXCEPTION.getCode(), ALREADY_EXCEPTION.getMessage());
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    @ResponseStatus(UNAUTHORIZED)
    public ErrorResponse handle(InvalidCredentialsException exception) {
        log.error("InvalidCredentialsException, ", exception);
        return new ErrorResponse(INVALID_CREDENTIALS.getCode(), INVALID_CREDENTIALS.getMessage());
    }

    @ExceptionHandler(IncorrectOldPasswordException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handle(IncorrectOldPasswordException exception) {
        log.error("IncorrectOldPasswordException, ", exception);
        return new ErrorResponse(INCORRECT_OLD_PASSWORD.getCode(), INCORRECT_OLD_PASSWORD.getMessage());
    }

    @ExceptionHandler(ConfirmPasswordException.class)
    @ResponseStatus(CONFLICT)
    public ErrorResponse handle(ConfirmPasswordException exception) {
        log.error("ConfirmPasswordException, ", exception);
        return new ErrorResponse(CONFIRM_PASSWORD.getCode(), CONFIRM_PASSWORD.getMessage());
    }
}