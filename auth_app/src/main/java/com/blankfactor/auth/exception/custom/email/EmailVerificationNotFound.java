package com.blankfactor.auth.exception.custom.email;

public class EmailVerificationNotFound extends RuntimeException {
    public EmailVerificationNotFound(String message) {
        super(message);
    }
}
