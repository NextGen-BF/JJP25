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

    private static final String TEST_EMAIL = "example@email.com";
    private static final String TEST_PASSWORD = "Password1!";
    private static final String TEST_USERNAME = "john_doe";
    private static final String TEST_FIRST_NAME = "John";
    private static final String TEST_LAST_NAME = "Doe";
    private static final String TEST_BIRTHDATE = "2000-01-01T01:01:01";
    private static final String TEST_ATTENDEE_ROLE = "attendee";
    private static final String TEST_ORGANISER_ROLE = "organiser";
    private static final String TEST_VERIFICATION_CODE = "123456";
    private static final String VALID_TOKEN = "validToken";
    private static final String NEW_PASSWORD = "newPassword1!";
    private static final String DIFFERENT_PASSWORD = "differentPassword";

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
            RegisterRequest registerRequest = RegisterRequest.builder()
                    .email(TEST_EMAIL)
                    .password(TEST_PASSWORD)
                    .confirmPassword(TEST_PASSWORD)
                    .username(TEST_USERNAME)
                    .firstName(TEST_FIRST_NAME)
                    .lastName(TEST_LAST_NAME)
                    .birthDate(LocalDateTime.parse(TEST_BIRTHDATE))
                    .role(TEST_ATTENDEE_ROLE)
                    .build();

            // When
            RegisterResponse registerResponse = authService.register(registerRequest);

            // Then
            assertTrue(userRepository.findByEmail(registerRequest.getEmail()).isPresent());
            assertEquals(registerResponse.getEmail(), registerRequest.getEmail());
            assertEquals(registerResponse.getUsername(), registerRequest.getUsername());
            assertEquals(registerResponse.getFirstName(), registerRequest.getFirstName());
            assertEquals(registerResponse.getLastName(), registerRequest.getLastName());
            assertFalse(registerResponse.getEnabled());
            assertNotNull(registerResponse.getRoles());
            assertEquals(1, registerResponse.getRoles().size());
            assertTrue(registerResponse.getRoles().contains("ROLE_USER"));
        }

        @Test
        void shouldSuccessfullyRegisterAdmin() {
            // Given
            RegisterRequest registerRequest = RegisterRequest.builder()
                    .email(TEST_EMAIL)
                    .password(TEST_PASSWORD)
                    .confirmPassword(TEST_PASSWORD)
                    .username(TEST_USERNAME)
                    .firstName(TEST_FIRST_NAME)
                    .lastName(TEST_LAST_NAME)
                    .birthDate(LocalDateTime.parse(TEST_BIRTHDATE))
                    .role(TEST_ORGANISER_ROLE)
                    .build();

            // When
            RegisterResponse registerResponse = authService.register(registerRequest);

            // Then
            assertTrue(userRepository.findByEmail(registerRequest.getEmail()).isPresent());
            assertEquals(registerResponse.getEmail(), registerRequest.getEmail());
            assertEquals(registerResponse.getUsername(), registerRequest.getUsername());
            assertEquals(registerResponse.getFirstName(), registerRequest.getFirstName());
            assertEquals(registerResponse.getLastName(), registerRequest.getLastName());
            assertFalse(registerResponse.getEnabled());
            assertNotNull(registerResponse.getRoles());
            assertEquals(2, registerResponse.getRoles().size());
            assertTrue(registerResponse.getRoles().contains("ROLE_USER"));
            assertTrue(registerResponse.getRoles().contains("ROLE_ADMIN"));
        }

        @Test
        void shouldThrowUserFoundExceptionWhenEmailIsAlreadyInUse() {
            // Given
            RegisterRequest registerRequest = RegisterRequest.builder()
                    .email(TEST_EMAIL)
                    .password(TEST_PASSWORD)
                    .confirmPassword(TEST_PASSWORD)
                    .username(TEST_USERNAME)
                    .firstName(TEST_FIRST_NAME)
                    .lastName(TEST_LAST_NAME)
                    .birthDate(LocalDateTime.parse(TEST_BIRTHDATE))
                    .role(TEST_ATTENDEE_ROLE)
                    .build();

            // When
            authService.register(registerRequest);

            // Then
            UserFoundException exception = assertThrows(UserFoundException.class,
                    () -> authService.register(registerRequest));

            assertEquals(TEST_EMAIL + " is already in use", exception.getMessage());
        }

        @Test
        void shouldThrowUserFoundExceptionWhenUsernameIsAlreadyInUse() {
            // Given
            RegisterRequest registerRequest = RegisterRequest.builder()
                    .email(TEST_EMAIL)
                    .password(TEST_PASSWORD)
                    .confirmPassword(TEST_PASSWORD)
                    .username(TEST_USERNAME)
                    .firstName(TEST_FIRST_NAME)
                    .lastName(TEST_LAST_NAME)
                    .birthDate(LocalDateTime.parse(TEST_BIRTHDATE))
                    .role(TEST_ATTENDEE_ROLE)
                    .build();

            // When
            authService.register(registerRequest);
            registerRequest.setEmail("new@email.com");

            // Then
            UserFoundException exception = assertThrows(UserFoundException.class,
                    () -> authService.register(registerRequest));

            assertEquals(TEST_USERNAME + " is already in use", exception.getMessage());
        }

        @Test
        void shouldThrowPasswordDoNotMatchWhenPasswordsAreNotEqual() {
            // Given
            RegisterRequest registerRequest = RegisterRequest.builder()
                    .email(TEST_EMAIL)
                    .password(TEST_PASSWORD)
                    .confirmPassword("Password2!")
                    .username(TEST_USERNAME)
                    .firstName(TEST_FIRST_NAME)
                    .lastName(TEST_LAST_NAME)
                    .birthDate(LocalDateTime.parse(TEST_BIRTHDATE))
                    .role(TEST_ATTENDEE_ROLE)
                    .build();

            // When & Then
            PasswordsDoNotMatchException exception = assertThrows(PasswordsDoNotMatchException.class,
                    () -> authService.register(registerRequest));

            assertEquals("Passwords do not match. Please try again.", exception.getMessage());
        }
    }

    @Nested
    class VerifyTests {
        @Test // TODO: depends on ems_app, because makes an actual request
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
            authService.register(registerRequest);
            String code = userRepository.findByEmail(registerRequest.getEmail()).get().getVerificationCode();
            VerifyRequest verifyRequest = new VerifyRequest(registerRequest.getEmail(), code);

            // When
            VerifyResponse verifyResponse = authService.verify(verifyRequest);

            // Then
            assertTrue(verifyRequest.getEmail().equals(verifyResponse.getEmail()));
            assertTrue(verifyResponse.getEnabled());
        }

        @Test
        void shouldThrowUserNotFoundExceptionWhenEmailIsNotFound() {
            // Given
            VerifyRequest verifyRequest = new VerifyRequest(TEST_EMAIL, TEST_VERIFICATION_CODE);

            // When & Then
            UserNotFoundException userNotFoundException = assertThrows(UserNotFoundException.class, () -> authService.verify(verifyRequest));
            assertTrue(userNotFoundException.getMessage().equals(verifyRequest.getEmail() + " is not found"));
        }

        @Test // TODO: depends on ems_app, because makes an actual request
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
            authService.register(registerRequest);
            String code = userRepository.findByEmail(registerRequest.getEmail()).get().getVerificationCode();
            VerifyRequest verifyRequest = new VerifyRequest(registerRequest.getEmail(), code);
            authService.verify(verifyRequest);

            // When & Then
            UserVerifiedException userVerifiedException = assertThrows(UserVerifiedException.class, () -> authService.verify(verifyRequest));
            assertTrue(userVerifiedException.getMessage().equals(verifyRequest.getEmail() + " is already verified"));
        }

        @Test
        void shouldThrowIncorrectVerificationCodeExceptionWhenTheCodeIsIncorrect() {
            // Given
            RegisterRequest registerRequest = RegisterRequest.builder()
                    .email(TEST_EMAIL)
                    .password(TEST_PASSWORD)
                    .confirmPassword(TEST_PASSWORD)
                    .username(TEST_USERNAME)
                    .firstName(TEST_FIRST_NAME)
                    .lastName(TEST_LAST_NAME)
                    .birthDate(LocalDateTime.parse(TEST_BIRTHDATE))
                    .role(TEST_ATTENDEE_ROLE)
                    .build();
            authService.register(registerRequest);
            VerifyRequest verifyRequest = new VerifyRequest(registerRequest.getEmail(), "000000");

            // When & Then
            IncorrectVerificationCodeException incorrectVerificationCodeException = assertThrows(IncorrectVerificationCodeException.class, () -> authService.verify(verifyRequest));
            assertEquals("Incorrect verification code: 000000", incorrectVerificationCodeException.getMessage());
        }

        @Test
        void shouldThrowExpiredVerificationCodeExceptionWhenTheCodeHasExpired() {
            // Given
            RegisterRequest registerRequest = RegisterRequest.builder()
                    .email(TEST_EMAIL)
                    .password(TEST_PASSWORD)
                    .confirmPassword(TEST_PASSWORD)
                    .username(TEST_USERNAME)
                    .firstName(TEST_FIRST_NAME)
                    .lastName(TEST_LAST_NAME)
                    .birthDate(LocalDateTime.parse(TEST_BIRTHDATE))
                    .role(TEST_ATTENDEE_ROLE)
                    .build();
            authService.register(registerRequest);
            User user = userRepository.findByEmail(registerRequest.getEmail()).get();
            String code = user.getVerificationCode();
            VerifyRequest verifyRequest = new VerifyRequest(registerRequest.getEmail(), code);
            user.setVerificationCodeExpiresAt(user.getVerificationCodeExpiresAt().minusMinutes(120));
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
            RegisterRequest registerRequest = RegisterRequest.builder()
                    .email(TEST_EMAIL)
                    .password(TEST_PASSWORD)
                    .confirmPassword(TEST_PASSWORD)
                    .username(TEST_USERNAME)
                    .firstName(TEST_FIRST_NAME)
                    .lastName(TEST_LAST_NAME)
                    .birthDate(LocalDateTime.parse(TEST_BIRTHDATE))
                    .role(TEST_ATTENDEE_ROLE)
                    .build();
            RegisterResponse registerResponse = authService.register(registerRequest);
            String code = userRepository.findByEmail(registerRequest.getEmail()).get().getVerificationCode();

            // When
            String newCode = authService.resend(registerResponse.getEmail());

            // Then
            assertNotEquals(code, newCode);
            assertEquals(userRepository.findByEmail(registerRequest.getEmail()).get()
                    .getVerificationCode(), newCode);
        }

        @Test
        void shouldThrowUserNotFoundExceptionWhenTheEmailIsNotFound() {
            // Given & When & Then
            UserNotFoundException userNotFoundException = assertThrows(UserNotFoundException.class, () -> authService.resend(TEST_EMAIL));
            assertEquals(TEST_EMAIL + " is not found", userNotFoundException.getMessage());
        }

        @Test // TODO: depends on ems_app, because makes an actual request
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
            authService.register(registerRequest);
            String code = userRepository.findByEmail(registerRequest.getEmail()).get().getVerificationCode();
            VerifyRequest verifyRequest = new VerifyRequest(
                    registerRequest.getEmail(),
                    code);
            authService.verify(verifyRequest);

            // When & Then
            UserVerifiedException userVerifiedException = assertThrows(UserVerifiedException.class, () -> authService.verify(verifyRequest));
            assertTrue(userVerifiedException.getMessage().equals(verifyRequest.getEmail() + " is already verified"));
        }
    }

}
