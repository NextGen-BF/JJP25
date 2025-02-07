package com.blankfactor.auth.exception;

import com.blankfactor.auth.exception.custom.*;
import com.blankfactor.auth.exception.custom.code.ExpiredVerificationCodeException;
import com.blankfactor.auth.exception.custom.code.IncorrectVerificationCodeException;
import com.blankfactor.auth.exception.custom.user.UserFoundException;
import com.blankfactor.auth.exception.custom.user.UserNotFoundException;
import com.blankfactor.auth.exception.custom.user.UserVerifiedException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({IncorrectVerificationCodeException.class, PasswordsDoNotMatchException.class})
    public ResponseEntity<Map<String, String>> handleCustomExceptions(RuntimeException ex) {
        return new ResponseEntity<>(getErrorsMap("400", "BAD_REQUEST", ex.getMessage()), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({UserNotFoundException.class})
    public ResponseEntity<Map<String, String>> handleUserNotFoundException(RuntimeException ex) {
        return new ResponseEntity<>(getErrorsMap("404", "NOT_FOUND", ex.getMessage()), new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({UserVerifiedException.class, UserFoundException.class})
    public ResponseEntity<Map<String, String>> handleUserVerifiedException(RuntimeException ex) {
        return new ResponseEntity<>(getErrorsMap("409", "CONFLICT", ex.getMessage()), new HttpHeaders(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler({ExpiredVerificationCodeException.class})
    public ResponseEntity<Map<String, String>> handleExpiredVerificationCodeException(RuntimeException ex) {
        return new ResponseEntity<>(getErrorsMap("410", "GONE", ex.getMessage()), new HttpHeaders(), HttpStatus.GONE);
    }

    @ExceptionHandler({Exception.class, VerificationEmailNotSentException.class})
    public ResponseEntity<Map<String, String>> handleVerificationEmailNotSentException(Exception ex) {
        return new ResponseEntity<>(getErrorsMap("500", "INTERNAL_SERVER_ERROR", ex.getMessage()), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, List<String>>> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors()
                .stream().map(FieldError::getDefaultMessage).collect(Collectors.toList());
        return new ResponseEntity<>(getErrorsMap(errors), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    private Map<String, List<String>> getErrorsMap(List<String> errors) {
        Map<String, List<String>> errorResponse = new HashMap<>();
        errorResponse.put("errors", errors);
        return errorResponse;
    }

    private Map<String, String> getErrorsMap(String code, String title, String message) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("code", code);
        errorResponse.put("title", title);
        errorResponse.put("message", message);
        errorResponse.put("timestamp", String.valueOf(LocalDateTime.now()));
        return errorResponse;
    }

}
