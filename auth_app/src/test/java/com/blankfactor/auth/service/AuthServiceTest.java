package com.blankfactor.auth.service;

import com.blankfactor.auth.entity.User;
import com.blankfactor.auth.entity.dto.exp.RegisterResponse;
import com.blankfactor.auth.entity.dto.imp.RegisterRequest;
import com.blankfactor.auth.exception.custom.PasswordsDoNotMatchException;
import com.blankfactor.auth.exception.custom.VerificationEmailNotSentException;
import com.blankfactor.auth.exception.custom.user.UserFoundException;
import com.blankfactor.auth.repository.UserRepository;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.thymeleaf.TemplateEngine;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @Mock
    private TemplateEngine templateEngine;

    @Mock
    private ModelMapper modelMapper;

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
        when(this.userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(existingUser));

        // Then
        UserFoundException exception = assertThrows(UserFoundException.class, () -> {
            this.authService.validateCredentials(request);
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
        when(this.userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        User existingUser = new User();
        existingUser.setUsername(request.getUsername());
        when(this.userRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(existingUser));

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
        when(this.userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(this.userRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());

        // Then
        PasswordsDoNotMatchException exception = assertThrows(PasswordsDoNotMatchException.class, () -> {
            authService.validateCredentials(request);
        });
        assertEquals("Passwords do not match. Please try again.", exception.getMessage());
    }

    @Test
    public void testRegister_Success() throws MessagingException {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");
        request.setConfirmPassword("password");
        request.setUsername("testuser");
        request.setFirstName("Test");
        request.setLastName("User");
        request.setBirthDate(LocalDateTime.parse("2000-01-01T01:01:01"));

        when(this.userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(this.userRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());
        when(this.passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(this.modelMapper.map(any(User.class), eq(RegisterResponse.class))).thenReturn(new RegisterResponse());
        when(this.templateEngine.process(anyString(), any())).thenReturn("Mocked Email Content");

        // When
        RegisterResponse response = this.authService.register(request);

        // Then
        verify(this.userRepository).saveAndFlush(any(User.class));
        verify(emailService).sendVerificationEmail(eq(request.getEmail()), eq("Account Verification"), eq("Mocked Email Content"));
        assertNotNull(response);
    }

    @Test
    public void testRegister_EmailSendingFailure() throws MessagingException {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setUsername("testuser");
        request.setPassword("password");
        request.setConfirmPassword("password");
        request.setFirstName("Test");
        request.setLastName("User");
        request.setBirthDate(LocalDateTime.parse("2000-01-01T01:01:01"));

        // When
        when(this.userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(this.userRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());
        when(this.passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(templateEngine.process(anyString(), any())).thenReturn("Mocked Email Content");
        doThrow(new MessagingException("Failed to send email")).when(this.emailService).sendVerificationEmail(anyString(), anyString(), anyString());

        // Then
        VerificationEmailNotSentException exception = assertThrows(VerificationEmailNotSentException.class, () -> {
            this.authService.register(request);
        });
        assertEquals("Failed to send verification email to test@example.com", exception.getMessage());
    }

}