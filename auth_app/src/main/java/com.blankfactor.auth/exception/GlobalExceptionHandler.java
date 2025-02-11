package com.blankfactor.auth.exception;

import com.blankfactor.auth.exception.custom.InvalidPasswordException;
import com.blankfactor.auth.exception.custom.PasswordsDoNotMatchException;
import com.blankfactor.auth.exception.custom.VerificationEmailNotSentException;
import com.blankfactor.auth.exception.custom.user.UserNotVerifiedException;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;


public class GlobalExceptionHandler {

    @ExceptionHandler({
            IncorrectVerificationCodeException.class,
            PasswordsDoNotMatchException.class,
            JwtException.class,
            ExpiredJwtException.class,
            IllegalArgumentException.class})
    public ResponseEntity<Map<String, String>> handleCustomExceptions(RuntimeException ex) {
        log.error("Handled exception: {} - {}", ex.getClass().getSimpleName(), ex.getMessage(), ex);
        return new ResponseEntity<>(getErrorsMap("400", "BAD_REQUEST", ex.getMessage()), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotVerifiedException.class)
    public ResponseEntity<Map<String, String>> handleUserNotVerifiedException(UserNotVerifiedException ex){
        log.error("Handled exception: {} - {}", ex.getClass().getSimpleName(), ex.getMessage(), ex);
        return new ResponseEntity<>(getErrorsMap("403", "FORBIDDEN", ex.getMessage()), new HttpHeaders(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<Map<String, String>> handleInvalidPasswordException(InvalidPasswordException ex) {
        log.error("Handled exception: {} - {}", ex.getClass().getSimpleName(), ex.getMessage(), ex);
        return new ResponseEntity<>(getErrorsMap("401", "UNAUTHORIZED", ex.getMessage()), new HttpHeaders(), HttpStatus.UNAUTHORIZED);
    }

}