package com.blankfactor.auth.exception.custom;

public class NullVerificationCodeException extends RuntimeException {
    public NullVerificationCodeException(String message) {
        super(message);
    }
}
