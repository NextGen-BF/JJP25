package com.blankfactor.auth.exception.custom.user;

public class UserFoundException extends RuntimeException {
    public UserFoundException(String message) {
        super(message);
    }
}
