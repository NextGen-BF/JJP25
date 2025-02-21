package com.backend.exception;

import com.backend.exception.user.UserFoundException;
import lombok.extern.log4j.Log4j2;
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
@Log4j2
public class GlobalExceptionHandler {

    /* @ExceptionHandler()
    public ResponseEntity<Map<String, String>> handleBadRequests(RuntimeException ex) {
        log.error("Handled exception: {} - {}", ex.getClass().getSimpleName(), ex.getMessage(), ex);
        return new ResponseEntity<>(getErrorsMap("400", "BAD_REQUEST", ex.getMessage()), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    } */

    /* @ExceptionHandler()
    public ResponseEntity<Map<String, String>> handleUnauthorized(RuntimeException ex) {
        log.error("Handled exception: {} - {}", ex.getClass().getSimpleName(), ex.getMessage(), ex);
        return new ResponseEntity<>(getErrorsMap("401", "UNAUTHORIZED", ex.getMessage()), new HttpHeaders(), HttpStatus.UNAUTHORIZED);
    } */

    /* @ExceptionHandler()
    public ResponseEntity<Map<String, String>> handleForbidden(RuntimeException ex){
        log.error("Handled exception: {} - {}", ex.getClass().getSimpleName(), ex.getMessage(), ex);
        return new ResponseEntity<>(getErrorsMap("403", "FORBIDDEN", ex.getMessage()), new HttpHeaders(), HttpStatus.FORBIDDEN);
    } */

    /* @ExceptionHandler()
    public ResponseEntity<Map<String, String>> handleNotFound(RuntimeException ex) {
        log.error("Handled exception: {} - {}", ex.getClass().getSimpleName(), ex.getMessage(), ex);
        return new ResponseEntity<>(getErrorsMap("404", "NOT_FOUND", ex.getMessage()), new HttpHeaders(), HttpStatus.NOT_FOUND);
    } */

    @ExceptionHandler({UserFoundException.class})
    public ResponseEntity<Map<String, String>> handleConflict(RuntimeException ex) {
        log.error("Handled exception: {} - {}", ex.getClass().getSimpleName(), ex.getMessage(), ex);
        return new ResponseEntity<>(getErrorsMap("409", "CONFLICT", ex.getMessage()), new HttpHeaders(), HttpStatus.CONFLICT);
    }

    /* @ExceptionHandler()
    public ResponseEntity<Map<String, String>> handleGone(RuntimeException ex) {
        log.error("Handled exception: {} - {}", ex.getClass().getSimpleName(), ex.getMessage(), ex);
        return new ResponseEntity<>(getErrorsMap("410", "GONE", ex.getMessage()), new HttpHeaders(), HttpStatus.GONE);
    } */

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Map<String, String>> handleInternalServerError(Exception ex) {
        log.error("Handled exception: {} - {}", ex.getClass().getSimpleName(), ex.getMessage(), ex);
        return new ResponseEntity<>(getErrorsMap("500", "INTERNAL_SERVER_ERROR", ex.getMessage()), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, List<String>>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, List<String>> fieldErrorsMap = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.groupingBy(
                        FieldError::getField,
                        Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList())
                ));
        log.error("Validation errors: {}", fieldErrorsMap);
        return new ResponseEntity<>(fieldErrorsMap, new HttpHeaders(), HttpStatus.BAD_REQUEST);
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
