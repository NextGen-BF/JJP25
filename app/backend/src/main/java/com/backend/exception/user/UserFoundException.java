package com.backend.exception.user;

public class UserFoundException extends RuntimeException {
    public UserFoundException(String message) {
        super(message);
    }
}
