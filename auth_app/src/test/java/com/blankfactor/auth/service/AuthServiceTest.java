package com.blankfactor.auth.service;

import com.blankfactor.auth.entity.User;
import com.blankfactor.auth.entity.dto.exp.RegisterResponse;
import com.blankfactor.auth.entity.dto.imp.RegisterRequest;
import com.blankfactor.auth.entity.dto.imp.VerifyRequest;
import com.blankfactor.auth.exception.custom.PasswordsDoNotMatchException;
import com.blankfactor.auth.exception.custom.VerificationEmailNotSentException;
import com.blankfactor.auth.exception.custom.code.ExpiredVerificationCodeException;
import com.blankfactor.auth.exception.custom.code.IncorrectVerificationCodeException;
import com.blankfactor.auth.exception.custom.user.UserFoundException;
import com.blankfactor.auth.exception.custom.user.UserNotFoundException;
import com.blankfactor.auth.exception.custom.user.UserVerifiedException;
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
        UserFoundException exception = assertThrows(UserFoundException.class, () -> this.authService.validateCredentials(request));
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
        UserFoundException exception = assertThrows(UserFoundException.class, () -> authService.validateCredentials(request));
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
        PasswordsDoNotMatchException exception = assertThrows(PasswordsDoNotMatchException.class, () -> authService.validateCredentials(request));
        assertEquals("Passwords do not match. Please try again.", exception.getMessage());
    }

    @Test
    public void shouldSuccessfullyRegisterUser() throws MessagingException {
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
    public void shouldThrowVerificationEmailNotSentExceptionWhenEmailIsNotSent() throws MessagingException {
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
        VerificationEmailNotSentException exception = assertThrows(VerificationEmailNotSentException.class, () -> this.authService.register(request));
        assertEquals("Failed to send verification email to test@example.com", exception.getMessage());
    }

    @Test
    public void shouldThrowUserNotFoundExceptionWhenUserWithSuchEmailDoesNotExits() {
        // Given
        VerifyRequest verifyRequest = new VerifyRequest();
        String email = "test@example.com";
        verifyRequest.setEmail(email);
        verifyRequest.setVerificationCode("123456");

        // When
        when(this.userRepository.findByEmail(verifyRequest.getEmail())).thenReturn(Optional.empty());

        // Then
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> this.authService.verify(verifyRequest));
        assertEquals("test@example.com is not found", exception.getMessage());
    }

    @Test
    public void shouldThrowUserVerifiedExceptionWhenTheUserIsVerified() {
        // Given
        VerifyRequest verifyRequest = new VerifyRequest();
        String email = "test@example.com";
        verifyRequest.setEmail(email);
        verifyRequest.setVerificationCode("123456");
        User user = new User();
        user.setEnabled(true);

        // When
        when(this.userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Then
        UserVerifiedException exception = assertThrows(UserVerifiedException.class, () -> this.authService.verify(verifyRequest));
        assertEquals("test@example.com is already verified", exception.getMessage());
    }

    @Test
    public void shouldThrowIncorrectVerificationCodeExceptionWhenTheCodeIsIncorrect() {
        // Given
        VerifyRequest verifyRequest = new VerifyRequest();
        String email = "test@example.com";
        verifyRequest.setEmail(email);
        verifyRequest.setVerificationCode("123456");
        User user = new User();
        user.setEnabled(false);
        user.setVerificationCode("654321");

        // When
        when(this.userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Then
        IncorrectVerificationCodeException exception = assertThrows(IncorrectVerificationCodeException.class, () -> {
            authService.verify(verifyRequest);
        });
        assertEquals("Incorrect verification code: 123456", exception.getMessage());
    }

    @Test
    public void shouldThrowExpiredVerificationCodeExceptionWhenTheCodeHasExpired() {
        // Given
        VerifyRequest verifyRequest = new VerifyRequest();
        String email = "test@example.com";
        verifyRequest.setEmail(email);
        String code = "123456";
        verifyRequest.setVerificationCode(code);
        User user = new User();
        user.setEnabled(false);
        user.setVerificationCode(code);
        user.setVerificationCodeExpiresAt(LocalDateTime.now().minusMinutes(1));

        // When
        when(this.userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Then
        ExpiredVerificationCodeException exception = assertThrows(ExpiredVerificationCodeException.class, () -> {
            authService.verify(verifyRequest);
        });
        assertEquals("Verification code 123456 has expired", exception.getMessage());
    }

    @Test
    public void shouldSuccessfullyVerifyUser() {
        // Given
        VerifyRequest verifyRequest = new VerifyRequest();
        String email = "test@example.com";
        verifyRequest.setEmail(email);
        String code = "123456";
        verifyRequest.setVerificationCode(code);
        User user = new User();
        user.setEnabled(false);
        user.setVerificationCode(code);
        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(10));

        // When
        when(this.userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Then
        assertDoesNotThrow(() -> {
            authService.verify(verifyRequest);
        });
        assertTrue(user.isEnabled());
        assertNull(user.getVerificationCode());
        assertNull(user.getVerificationCodeExpiresAt());
        verify(this.userRepository).saveAndFlush(user);
    }

}