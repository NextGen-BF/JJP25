package com.blankfactor.auth.exception.custom.email;

public class VerificationEmailNotSentException extends RuntimeException {
    public VerificationEmailNotSentException(String message, Throwable error) {
        super(message, error);
    }
}
