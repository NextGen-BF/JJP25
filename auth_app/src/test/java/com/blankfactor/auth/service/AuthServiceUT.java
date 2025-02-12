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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.thymeleaf.TemplateEngine;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceUT {

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

    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_USERNAME = "testuser";

    @Nested
    class ValidateCredentialsTests {
        @Test
        void shouldThrowUserFoundExceptionWhenUserWithSuchEmailExists() {
            // Given
            RegisterRequest request = new RegisterRequest();
            request.setEmail(TEST_EMAIL);
            request.setUsername(TEST_USERNAME);

            // When
            User existingUser = new User();
            existingUser.setEmail(request.getEmail());
            when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(existingUser));

            // Then
            UserFoundException exception = assertThrows(UserFoundException.class, () -> authService.validateCredentials(request));
            assertEquals(TEST_EMAIL + " is already in use", exception.getMessage());
        }

        @Test
        void shouldThrowUserFoundExceptionWhenUserWithSuchUsernameExists() {
            // Given
            RegisterRequest request = new RegisterRequest();
            request.setEmail(TEST_EMAIL);
            request.setUsername(TEST_USERNAME);

            // When
            when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
            User existingUser = new User();
            existingUser.setUsername(request.getUsername());
            when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(existingUser));

            // Then
            UserFoundException exception = assertThrows(UserFoundException.class, () -> authService.validateCredentials(request));
            assertEquals(TEST_USERNAME + " is already in use", exception.getMessage());
        }

        @Test
        void shouldThrowPasswordsDoNotMatchExceptionWhenThePasswordsDoNotMatch() {
            // Given
            RegisterRequest request = new RegisterRequest();
            request.setEmail(TEST_EMAIL);
            request.setUsername(TEST_USERNAME);
            request.setPassword("password");
            request.setConfirmPassword("differentPassword");

            // When
            when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
            when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());

            // Then
            PasswordsDoNotMatchException exception = assertThrows(PasswordsDoNotMatchException.class, () -> authService.validateCredentials(request));
            assertEquals("Passwords do not match. Please try again.", exception.getMessage());
        }
    }

    @Nested
    class RegisterTests {
        @Test
        void shouldSuccessfullyRegisterUser() throws MessagingException {
            // Given
            RegisterRequest request = new RegisterRequest(
                    TEST_EMAIL,
                    "password",
                    "password",
                    TEST_USERNAME,
                    "Test",
                    "User",
                    LocalDateTime.parse("2000-01-01T01:01:01"),
                    "attendee"
            );

            when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
            when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());
            when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
            when(modelMapper.map(any(User.class), eq(RegisterResponse.class))).thenReturn(new RegisterResponse());
            when(templateEngine.process(anyString(), any())).thenReturn("Mocked Email Content");

            // When
            RegisterResponse response = authService.register(request);

            // Then
            verify(userRepository).saveAndFlush(any(User.class));
            verify(emailService).sendVerificationEmail(eq(request.getEmail()), eq("Account Verification"), eq("Mocked Email Content"));
            assertNotNull(response);
        }

        @Test
        void shouldThrowVerificationEmailNotSentExceptionWhenEmailIsNotSent() throws MessagingException {
            // Given
            RegisterRequest request = new RegisterRequest(
                    TEST_EMAIL,
                    "password",
                    "password",
                    TEST_USERNAME,
                    "Test",
                    "User",
                    LocalDateTime.parse("2000-01-01T01:01:01"),
                    "attendee"
            );

            // When
            when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
            when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());
            when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
            when(templateEngine.process(anyString(), any())).thenReturn("Mocked Email Content");
            doThrow(new MessagingException("Failed to send email")).when(emailService).sendVerificationEmail(anyString(), anyString(), anyString());

            // Then
            VerificationEmailNotSentException exception = assertThrows(VerificationEmailNotSentException.class, () -> authService.register(request));
            assertEquals("Failed to send verification email to test@example.com", exception.getMessage());
        }
    }

    @Nested
    class VerifyTests {
        @Test
        void shouldThrowUserNotFoundExceptionWhenUserWithSuchEmailDoesNotExits() {
            // Given
            VerifyRequest verifyRequest = new VerifyRequest();
            verifyRequest.setEmail(TEST_EMAIL);
            verifyRequest.setVerificationCode("123456");

            // When
            when(userRepository.findByEmail(verifyRequest.getEmail())).thenReturn(Optional.empty());

            // Then
            UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> authService.verify(verifyRequest));
            assertEquals(TEST_EMAIL + " is not found", exception.getMessage());
        }

        @Test
        void shouldThrowUserVerifiedExceptionWhenTheUserIsVerified() {
            // Given
            VerifyRequest verifyRequest = new VerifyRequest();
            verifyRequest.setEmail(TEST_EMAIL);
            verifyRequest.setVerificationCode("123456");
            User user = new User();
            user.setEnabled(true);

            // When
            when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));

            // Then
            UserVerifiedException exception = assertThrows(UserVerifiedException.class, () -> authService.verify(verifyRequest));
            assertEquals(TEST_EMAIL + " is already verified", exception.getMessage());
        }

        @Test
        void shouldThrowIncorrectVerificationCodeExceptionWhenTheCodeIsIncorrect() {
            // Given
            VerifyRequest verifyRequest = new VerifyRequest();
            verifyRequest.setEmail(TEST_EMAIL);
            verifyRequest.setVerificationCode("123456");
            User user = new User();
            user.setEnabled(false);
            user.setVerificationCode("654321");

            // When
            when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));

            // Then
            IncorrectVerificationCodeException exception = assertThrows(IncorrectVerificationCodeException.class, () -> authService.verify(verifyRequest));
            assertEquals("Incorrect verification code: 123456", exception.getMessage());
        }

        @Test
        void shouldThrowExpiredVerificationCodeExceptionWhenTheCodeHasExpired() {
            // Given
            VerifyRequest verifyRequest = new VerifyRequest();
            verifyRequest.setEmail(TEST_EMAIL);
            String code = "123456";
            verifyRequest.setVerificationCode(code);
            User user = new User();
            user.setEnabled(false);
            user.setVerificationCode(code);
            user.setVerificationCodeExpiresAt(LocalDateTime.now().minusMinutes(1));

            // When
            when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));

            // Then
            ExpiredVerificationCodeException exception = assertThrows(ExpiredVerificationCodeException.class, () -> authService.verify(verifyRequest));
            assertEquals("Verification code 123456 has expired", exception.getMessage());
        }

        @Test
        void shouldSuccessfullyVerifyUser() {
            // Given
            VerifyRequest verifyRequest = new VerifyRequest();
            verifyRequest.setEmail(TEST_EMAIL);
            String code = "123456";
            verifyRequest.setVerificationCode(code);
            User user = new User();
            user.setEnabled(false);
            user.setVerificationCode(code);
            user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(10));

            // When
            when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));

            // Then
            assertDoesNotThrow(() -> {
                authService.verify(verifyRequest);
            });
            assertTrue(user.isEnabled());
            assertNull(user.getVerificationCode());
            assertNull(user.getVerificationCodeExpiresAt());
            verify(userRepository).saveAndFlush(user);
        }
    }

    @Nested
    class ResendTests {
        @Test
        void shouldThrowUserNotFoundExceptionWhenUserWithEmailDoesNotExist() {
            // When
            when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());

            // Then
            UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> authService.resend(TEST_EMAIL));
            assertEquals(TEST_EMAIL + " is not found", exception.getMessage());
        }

        @Test
        void shouldThrowUserVerifiedExceptionWhenUserIsAlreadyVerified() {
            // Given
            User user = new User();
            user.setEmail(TEST_EMAIL);
            user.setEnabled(true);

            // When
            when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));

            // Then
            UserVerifiedException exception = assertThrows(UserVerifiedException.class, () -> authService.resend(TEST_EMAIL));
            assertEquals(TEST_EMAIL + " is already verified", exception.getMessage());
        }

        @Test
        void shouldSuccessfullyResendVerificationCode() throws MessagingException {
            // Given
            User user = new User();
            user.setEmail(TEST_EMAIL);
            user.setEnabled(false);

            // When
            when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));
            when(templateEngine.process(anyString(), any())).thenReturn("Mocked Email Content");
            authService.resend(TEST_EMAIL);

            // Then
            assertNotNull(user.getVerificationCode());
            assertTrue(user.getVerificationCodeExpiresAt().isAfter(LocalDateTime.now()));
            verify(userRepository).saveAndFlush(user);
            verify(emailService).sendVerificationEmail(eq(TEST_EMAIL), eq("Account Verification"), eq("Mocked Email Content"));
        }
    }

}