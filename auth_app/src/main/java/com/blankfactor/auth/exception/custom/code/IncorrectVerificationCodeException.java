package com.blankfactor.auth.exception.custom.code;

public class IncorrectVerificationCodeException extends RuntimeException {
    public IncorrectVerificationCodeException(String message) {
        super(message);
    }
}
