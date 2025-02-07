package com.blankfactor.auth.service;

import com.blankfactor.auth.entity.User;
import com.blankfactor.auth.entity.dto.imp.RegisterRequest;
import com.blankfactor.auth.exception.custom.PasswordsDoNotMatchException;
import com.blankfactor.auth.exception.custom.user.UserFoundException;
import com.blankfactor.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void shouldThrowUserFoundExceptionWhenUserWithSuchEmailExists() {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setUsername("testuser");

        // When
        User existingUser = new User();
        existingUser.setEmail(request.getEmail());
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(existingUser));

        // Then
        UserFoundException exception = assertThrows(UserFoundException.class, () -> {
            authService.validateCredentials(request);
        });
        assertEquals("test@example.com is already in use", exception.getMessage());
    }

    @Test
    public void shouldThrowUserFoundExceptionWhenUserWithSuchUsernameExists() {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setUsername("testuser");

        // When
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        User existingUser = new User();
        existingUser.setUsername(request.getUsername());
        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(existingUser));

        // Then
        UserFoundException exception = assertThrows(UserFoundException.class, () -> {
            authService.validateCredentials(request);
        });
        assertEquals("testuser is already in use", exception.getMessage());
    }

    @Test
    public void shouldThrowPasswordsDoNotMatchExceptionWhenThePasswordsDoNotMatch() {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setUsername("testuser");
        request.setPassword("password");
        request.setConfirmPassword("differentPassword");

        // When
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());

        // Then
        PasswordsDoNotMatchException exception = assertThrows(PasswordsDoNotMatchException.class, () -> {
            authService.validateCredentials(request);
        });
        assertEquals("Passwords do not match. Please try again.", exception.getMessage());
    }

}