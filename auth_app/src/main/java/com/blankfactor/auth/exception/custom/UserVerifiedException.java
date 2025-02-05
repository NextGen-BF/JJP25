package com.blankfactor.auth.exception.custom;

public class UserVerifiedException extends RuntimeException {
    public UserVerifiedException(String message) {
        super(message);
    }
}
