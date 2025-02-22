package com.blankfactor.auth.exception.custom.user;

public class UserExistsException extends RuntimeException {
    public UserExistsException(String message) {
        super(message);
    }
}
