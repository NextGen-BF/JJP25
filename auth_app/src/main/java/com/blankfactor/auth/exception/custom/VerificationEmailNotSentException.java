package com.blankfactor.auth.exception.custom;

public class VerificationEmailNotSentException extends RuntimeException {
    public VerificationEmailNotSentException(String message, Throwable error) {
        super(message, error);
    }
}
