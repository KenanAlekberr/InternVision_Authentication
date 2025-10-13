package com.example.authsystem.exception.custom;

import lombok.Getter;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Getter
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class AlreadyExistException extends RuntimeException {
    String code;

    public AlreadyExistException(String code, String message) {
        super(message);
        this.code = code;
    }
}