package com.blankfactor.auth.service;

import com.blankfactor.auth.entity.User;
import com.blankfactor.auth.entity.dto.exp.RegisterResponse;
import com.blankfactor.auth.entity.dto.exp.VerifyResponse;
import com.blankfactor.auth.entity.dto.imp.RegisterRequest;
import com.blankfactor.auth.entity.dto.imp.VerifyRequest;
import com.blankfactor.auth.exception.custom.PasswordsDoNotMatchException;
import com.blankfactor.auth.exception.custom.code.ExpiredVerificationCodeException;
import com.blankfactor.auth.exception.custom.code.IncorrectVerificationCodeException;
import com.blankfactor.auth.exception.custom.user.UserFoundException;
import com.blankfactor.auth.exception.custom.user.UserNotFoundException;
import com.blankfactor.auth.exception.custom.user.UserVerifiedException;
import com.blankfactor.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class AuthServiceIT {

    private final AuthService authService;
    private final UserRepository userRepository;

    @Autowired
    public AuthServiceIT(AuthService authService, UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Nested
    class RegisterTests {
        @Test
        void shouldSuccessfullyRegisterUser() {
            // Given
            RegisterRequest registerRequest = new RegisterRequest(
                    "example@email.com",
                    "Password1!",
                    "Password1!",
                    "john_doe",
                    "John",
                    "Doe",
                    LocalDateTime.parse("2000-01-01T01:01:01"),
                    "attendee");

            // When
            RegisterResponse registerResponse = authService.register(registerRequest);

            // Then
            assertTrue(userRepository.findByEmail(registerRequest.getEmail()).isPresent());
            assertTrue(registerResponse.getEmail().equals(registerRequest.getEmail()));
            assertTrue(registerResponse.getPassword().matches("^\\$2[ayb]\\$\\d{2}\\$[./A-Za-z0-9]{53}$"));
            assertTrue(registerResponse.getUsername().equals(registerRequest.getUsername()));
            assertTrue(registerResponse.getFirstName().equals(registerRequest.getFirstName()));
            assertTrue(registerResponse.getLastName().equals(registerRequest.getLastName()));
            assertTrue(registerResponse.getBirthDate().isEqual(registerRequest.getBirthDate()));
            assertFalse(registerResponse.getEnabled());
            assertNotNull(registerResponse.getVerificationCode());
            assertNotNull(registerResponse.getVerificationCodeExpiresAt());
            assertNotNull(registerResponse.getRoles());
            assertTrue(registerResponse.getRoles().size() == 1);
            assertTrue(registerResponse.getRoles().contains("ROLE_USER"));
        }

        @Test
        void shouldSuccessfullyRegisterAdmin() {
            // Given
            RegisterRequest registerRequest = new RegisterRequest(
                    "example@email.com",
                    "Password1!",
                    "Password1!",
                    "john_doe",
                    "John",
                    "Doe",
                    LocalDateTime.parse("2000-01-01T01:01:01"),
                    "organiser");

            // When
            RegisterResponse registerResponse = authService.register(registerRequest);

            // Then
            assertTrue(userRepository.findByEmail(registerRequest.getEmail()).isPresent());
            assertTrue(registerResponse.getEmail().equals(registerRequest.getEmail()));
            assertTrue(registerResponse.getPassword().matches("^\\$2[ayb]\\$\\d{2}\\$[./A-Za-z0-9]{53}$"));
            assertTrue(registerResponse.getUsername().equals(registerRequest.getUsername()));
            assertTrue(registerResponse.getFirstName().equals(registerRequest.getFirstName()));
            assertTrue(registerResponse.getLastName().equals(registerRequest.getLastName()));
            assertTrue(registerResponse.getBirthDate().isEqual(registerRequest.getBirthDate()));
            assertFalse(registerResponse.getEnabled());
            assertNotNull(registerResponse.getVerificationCode());
            assertNotNull(registerResponse.getVerificationCodeExpiresAt());
            assertNotNull(registerResponse.getRoles());
            assertTrue(registerResponse.getRoles().size() == 2);
            assertTrue(registerResponse.getRoles().contains("ROLE_USER"));
            assertTrue(registerResponse.getRoles().contains("ROLE_ADMIN"));
        }

        @Test
        void shouldThrowUserFoundExceptionWhenEmailIsAlreadyInUse() {
            // Given
            RegisterRequest registerRequest = new RegisterRequest(
                    "example@email.com",
                    "Password1!",
                    "Password1!",
                    "john_doe",
                    "John",
                    "Doe",
                    LocalDateTime.parse("2000-01-01T01:01:01"),
                    "attendee");

            // When
            authService.register(registerRequest);

            // Then
            UserFoundException exception = assertThrows(UserFoundException.class,
                    () -> authService.register(registerRequest));

            assertEquals("example@email.com is already in use", exception.getMessage());
        }

        @Test
        void shouldThrowUserFoundExceptionWhenUsernameIsAlreadyInUse() {
            // Given
            RegisterRequest registerRequest = new RegisterRequest(
                    "example@email.com",
                    "Password1!",
                    "Password1!",
                    "john_doe",
                    "John",
                    "Doe",
                    LocalDateTime.parse("2000-01-01T01:01:01"),
                    "attendee");

            // When
            authService.register(registerRequest);
            registerRequest.setEmail("new@email.com");

            // Then
            UserFoundException exception = assertThrows(UserFoundException.class,
                    () -> authService.register(registerRequest));

            assertEquals("john_doe is already in use", exception.getMessage());
        }

        @Test
        void shouldThrowPasswordDoNotMatchWhenPasswordsAreNotEqual() {
            // Given
            RegisterRequest registerRequest = new RegisterRequest(
                    "example@email.com",
                    "Password1!",
                    "Password2!",
                    "john_doe",
                    "John",
                    "Doe",
                    LocalDateTime.parse("2000-01-01T01:01:01"),
                    "attendee");

            // When & Then
            PasswordsDoNotMatchException exception = assertThrows(PasswordsDoNotMatchException.class,
                    () -> authService.register(registerRequest));

            assertEquals("Passwords do not match. Please try again.", exception.getMessage());
        }
    }

    @Nested
    class VerifyTests {
        @Test
        void shouldSuccessfullyVerifyUser() {
            // Given
            RegisterRequest registerRequest = new RegisterRequest(
                    "example@email.com",
                    "Password1!",
                    "Password1!",
                    "john_doe",
                    "John",
                    "Doe",
                    LocalDateTime.parse("2000-01-01T01:01:01"),
                    "attendee");
            RegisterResponse registerResponse = authService.register(registerRequest);
            VerifyRequest verifyRequest = new VerifyRequest(
                    registerRequest.getEmail(),
                    registerResponse.getVerificationCode());

            // When
            VerifyResponse verifyResponse = authService.verify(verifyRequest);

            // Then
            assertTrue(verifyRequest.getEmail().equals(verifyResponse.getEmail()));
            assertTrue(verifyResponse.getEnabled());
            assertNull(verifyResponse.getVerificationCode());
            assertNull(verifyResponse.getVerificationCodeExpiresAt());
        }

        @Test
        void shouldThrowUserNotFoundExceptionWhenEmailIsNotFound() {
            // Given
            VerifyRequest verifyRequest = new VerifyRequest(
                    "not_found@email.com",
                    "000000"
            );

            // When & Then
            UserNotFoundException userNotFoundException = assertThrows(UserNotFoundException.class, () -> authService.verify(verifyRequest));
            assertTrue(userNotFoundException.getMessage().equals(verifyRequest.getEmail() + " is not found"));
        }

        @Test
        void shouldThrowUserVerifiedExceptionWhenTheUserIsAlreadyVerified() {
            // Given
            RegisterRequest registerRequest = new RegisterRequest(
                    "example@email.com",
                    "Password1!",
                    "Password1!",
                    "john_doe",
                    "John",
                    "Doe",
                    LocalDateTime.parse("2000-01-01T01:01:01"),
                    "attendee");
            RegisterResponse registerResponse = authService.register(registerRequest);
            VerifyRequest verifyRequest = new VerifyRequest(
                    registerRequest.getEmail(),
                    registerResponse.getVerificationCode());
            authService.verify(verifyRequest);

            // When & Then
            UserVerifiedException userVerifiedException = assertThrows(UserVerifiedException.class, () -> authService.verify(verifyRequest));
            assertTrue(userVerifiedException.getMessage().equals(verifyRequest.getEmail() + " is already verified"));
        }

        @Test
        void shouldThrowIncorrectVerificationCodeExceptionWhenTheCodeIsIncorrect() {
            // Given
            RegisterRequest registerRequest = new RegisterRequest(
                    "example@email.com",
                    "Password1!",
                    "Password1!",
                    "john_doe",
                    "John",
                    "Doe",
                    LocalDateTime.parse("2000-01-01T01:01:01"),
                    "attendee");
            RegisterResponse registerResponse = authService.register(registerRequest);
            VerifyRequest verifyRequest = new VerifyRequest(
                    registerRequest.getEmail(),
                    "000000");

            // When & Then
            IncorrectVerificationCodeException incorrectVerificationCodeException = assertThrows(IncorrectVerificationCodeException.class, () -> authService.verify(verifyRequest));
            assertTrue(incorrectVerificationCodeException.getMessage().equals("Incorrect verification code: 000000"));
        }

        @Test
        void shouldThrowExpiredVerificationCodeExceptionWhenTheCodeHasExpired() {
            // Given
            RegisterRequest registerRequest = new RegisterRequest(
                    "example@email.com",
                    "Password1!",
                    "Password1!",
                    "john_doe",
                    "John",
                    "Doe",
                    LocalDateTime.parse("2000-01-01T01:01:01"),
                    "attendee");
            RegisterResponse registerResponse = authService.register(registerRequest);
            VerifyRequest verifyRequest = new VerifyRequest(
                    registerRequest.getEmail(),
                    registerResponse.getVerificationCode());
            User user = userRepository.findByEmail(registerRequest.getEmail()).get();
            user.setVerificationCodeExpiresAt(registerResponse.getVerificationCodeExpiresAt().minusMinutes(120));
            userRepository.saveAndFlush(user);

            // When & Then
            ExpiredVerificationCodeException expiredVerificationCodeException = assertThrows(ExpiredVerificationCodeException.class, () -> authService.verify(verifyRequest));
            assertTrue(expiredVerificationCodeException.getMessage().equals("Verification code " + verifyRequest.getVerificationCode() + " has expired"));
        }
    }

    @Nested
    class ResendTests {
        @Test
        void shouldSuccessfullyResendVerificationCode() {
            // Given
            RegisterRequest registerRequest = new RegisterRequest(
                    "example@email.com",
                    "Password1!",
                    "Password1!",
                    "john_doe",
                    "John",
                    "Doe",
                    LocalDateTime.parse("2000-01-01T01:01:01"),
                    "attendee");
            RegisterResponse registerResponse = authService.register(registerRequest);

            // When
            String newCode = authService.resend(registerResponse.getEmail());

            // Then
            assertFalse(registerResponse.getVerificationCode().equals(newCode));
            assertTrue(userRepository.findByEmail(registerRequest.getEmail()).get()
                    .getVerificationCode().equals(newCode));
        }

        @Test
        void shouldThrowUserNotFoundExceptionWhenTheEmailIsNotFound() {
            // Given
            String email = "not_found@email.com";

            // When & Then
            UserNotFoundException userNotFoundException = assertThrows(UserNotFoundException.class, () -> authService.resend(email));
            assertTrue(userNotFoundException.getMessage().equals(email + " is not found"));
        }

        @Test
        void shouldThrowUserVerifiedExceptionWhenTheUserIsAlreadyVerified() {
            // Given
            RegisterRequest registerRequest = new RegisterRequest(
                    "example@email.com",
                    "Password1!",
                    "Password1!",
                    "john_doe",
                    "John",
                    "Doe",
                    LocalDateTime.parse("2000-01-01T01:01:01"),
                    "attendee");
            RegisterResponse registerResponse = authService.register(registerRequest);
            VerifyRequest verifyRequest = new VerifyRequest(
                    registerRequest.getEmail(),
                    registerResponse.getVerificationCode());
            authService.verify(verifyRequest);

            // When & Then
            UserVerifiedException userVerifiedException = assertThrows(UserVerifiedException.class, () -> authService.verify(verifyRequest));
            assertTrue(userVerifiedException.getMessage().equals(verifyRequest.getEmail() + " is already verified"));
        }
    }

}
