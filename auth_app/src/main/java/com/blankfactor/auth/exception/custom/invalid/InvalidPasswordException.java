package com.blankfactor.auth.exception.custom.invalid;

public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException(String message) {
        super(message);
    }
}
