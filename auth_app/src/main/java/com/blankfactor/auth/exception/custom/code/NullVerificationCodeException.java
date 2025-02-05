package com.blankfactor.auth.exception.custom.code;

public class NullVerificationCodeException extends RuntimeException {
    public NullVerificationCodeException(String message) {
        super(message);
    }
}
