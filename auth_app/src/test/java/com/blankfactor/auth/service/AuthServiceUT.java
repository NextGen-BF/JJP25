package com.blankfactor.auth.service;

import com.blankfactor.auth.entity.User;
import com.blankfactor.auth.entity.dto.exp.RegisterResponse;
import com.blankfactor.auth.entity.dto.exp.VerifyResponse;
import com.blankfactor.auth.entity.dto.imp.LoginRequest;
import com.blankfactor.auth.entity.dto.imp.RegisterRequest;
import com.blankfactor.auth.entity.dto.imp.VerifyRequest;
import com.blankfactor.auth.exception.custom.*;
import com.blankfactor.auth.exception.custom.code.ExpiredVerificationCodeException;
import com.blankfactor.auth.exception.custom.code.IncorrectVerificationCodeException;
import com.blankfactor.auth.exception.custom.user.*;
import com.blankfactor.auth.repository.UserRepository;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
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

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private RestClient restClient;

    @InjectMocks
    private AuthService authService;

    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_USERNAME = "testuser";
    private static final String VALID_TOKEN = "validToken";
    private static final String NEW_PASSWORD = "newPassword1!";
    private static final String DIFFERENT_PASSWORD = "differentPassword";

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
        }

        @Test
        void shouldThrowServiceUnavailableException() {
            // Given
            VerifyRequest verifyRequest = new VerifyRequest(TEST_EMAIL, "123456");
            User user = new User();
            user.setEmail(TEST_EMAIL);
            user.setEnabled(false);
            user.setVerificationCode("123456");
            user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));

            // When
            when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));
            when(userDetailsService.loadUserByUsername(user.getUsername())).thenReturn(user);
            doThrow(ResourceAccessException.class).when(restClient).post();

            // Then
            assertThrows(ServiceUnavailableException.class, () -> authService.verify(verifyRequest));
        }

        @Test
        void shouldThrowInvalidInformRequestException() {
            // Given
            VerifyRequest verifyRequest = new VerifyRequest(TEST_EMAIL, "123456");
            User user = new User();
            user.setEmail(TEST_EMAIL);
            user.setEnabled(false);
            user.setVerificationCode("123456");
            user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));

            // When
            when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));
            when(userDetailsService.loadUserByUsername(user.getUsername())).thenReturn(user);
            doThrow(InvalidInformRequestException.class).when(restClient).post();

            // Then
            assertThrows(InvalidInformRequestException.class, () -> authService.verify(verifyRequest));
        }

        @Test
        void shouldThrowUserExistsException() {
            // Given
            VerifyRequest verifyRequest = new VerifyRequest(TEST_EMAIL, "123456");
            User user = new User();
            user.setEmail(TEST_EMAIL);
            user.setEnabled(false);
            user.setVerificationCode("123456");
            user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));

            // When
            when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));
            when(userDetailsService.loadUserByUsername(user.getUsername())).thenReturn(user);
            doThrow(UserExistsException.class).when(restClient).post();

            // Then
            assertThrows(UserExistsException.class, () -> authService.verify(verifyRequest));
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

    @Nested
    class LoginTests {
        @Test
        void loginWithValidEmailReturnsUser() {
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setLoginIdentifier(TEST_EMAIL);
            loginRequest.setPassword("password");

            User user = User.builder()
                    .id(1L)
                    .email(TEST_EMAIL)
                    .username(TEST_USERNAME)
                    .password("encodedPassword")
                    .enabled(true)
                    .build();

            when(userRepository.findByEmailOrUsername(TEST_EMAIL))
                    .thenReturn(Optional.of(user));

            User result = authService.login(loginRequest);

            assertNotNull(result);
            assertEquals(user.getId(), result.getId());
            assertEquals(user.getEmail(), result.getEmail());
        }

        @Test
        void loginWithValidUsernameReturnsUser() {
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setLoginIdentifier(TEST_USERNAME);
            loginRequest.setPassword("password");

            User user = User.builder()
                    .id(1L)
                    .email(TEST_EMAIL)
                    .username(TEST_USERNAME)
                    .password("encodedPassword")
                    .enabled(true)
                    .build();

            when(userRepository.findByEmailOrUsername(TEST_USERNAME))
                    .thenReturn(Optional.of(user));

            User result = authService.login(loginRequest);

            assertNotNull(result);
            assertEquals(user.getId(), result.getId());
            assertEquals(user.getEmail(), result.getEmail());
        }

        @Test
        void loginWithNonExistentUserThrowsInvalidCredentialsException() {
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setLoginIdentifier("nonexistent@example.com");
            loginRequest.setPassword("password");

            when(userRepository.findByEmailOrUsername("nonexistent@example.com"))
                    .thenReturn(Optional.empty());

            assertThrows(InvalidCredentialsException.class, () -> authService.login(loginRequest));
        }

        @Test
        void loginWithUnverifiedUserThrowsUserNotVerifiedException() {
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setLoginIdentifier(TEST_EMAIL);
            loginRequest.setPassword("password");

            User user = User.builder()
                    .id(2L)
                    .email(TEST_EMAIL)
                    .username(TEST_USERNAME)
                    .password("encodedPassword")
                    .enabled(false)
                    .build();

            when(userRepository.findByEmailOrUsername(TEST_EMAIL))
                    .thenReturn(Optional.of(user));

            Exception exception = assertThrows(UserNotVerifiedException.class, () -> authService.login(loginRequest));
            assertTrue(exception.getMessage().contains("Account is not verified"));
        }

        @Test
        void loginWithInvalidCredentialsThrowsInvalidPasswordException() {
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setLoginIdentifier(TEST_EMAIL);
            loginRequest.setPassword("wrongPassword");

            User user = User.builder()
                    .id(3L)
                    .email(TEST_EMAIL)
                    .username(TEST_USERNAME)
                    .password("encodedPassword")
                    .enabled(true)
                    .build();

            when(userRepository.findByEmailOrUsername(TEST_EMAIL))
                    .thenReturn(Optional.of(user));

            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenThrow(new BadCredentialsException("Bad credentials"));

            assertThrows(InvalidCredentialsException.class, () -> authService.login(loginRequest));
        }

        @Test
        void loginWithNullIdentifierThrowsIllegalArgumentException() {
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setLoginIdentifier(null);
            loginRequest.setPassword("password");

            assertThrows(IllegalArgumentException.class, () -> authService.login(loginRequest));
        }

        @Test
        void loginWithEmptyIdentifierThrowsIllegalArgumentException() {
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setLoginIdentifier("");
            loginRequest.setPassword("password");

            assertThrows(IllegalArgumentException.class, () -> authService.login(loginRequest));
        }

        @Test
        void loginWithBlankIdentifierThrowsIllegalArgumentException() {
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setLoginIdentifier("   ");
            loginRequest.setPassword("password");

            assertThrows(IllegalArgumentException.class, () -> authService.login(loginRequest));
        }
    }

    @Nested
    class ResetPasswordTests {

        @Test
        void shouldSuccessfullyResetPassword() {
            String token = VALID_TOKEN;
            String newPassword = NEW_PASSWORD;
            String username = TEST_USERNAME;
            User user = new User();
            user.setUsername(username);

            when(jwtService.extractUsername(token)).thenReturn(username);
            when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

            authService.resetPassword(token, newPassword, NEW_PASSWORD);

            verify(passwordEncoder).encode(newPassword);
            verify(userRepository).saveAndFlush(user);
            assertEquals(passwordEncoder.encode(newPassword), user.getPassword());
        }

        @Test
        void shouldThrowPasswordsDoNotMatchExceptionWhenPasswordsDoNotMatch() {

            assertThrows(PasswordsDoNotMatchException.class,
                    () -> authService.resetPassword(VALID_TOKEN, NEW_PASSWORD, DIFFERENT_PASSWORD));
        }

        @Test
        void shouldThrowUserNotFoundExceptionWhenUserIsNotFound() {
            String token = VALID_TOKEN;
            String username = TEST_USERNAME;

            when(jwtService.extractUsername(token)).thenReturn(username);
            when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class,
                    () -> authService.resetPassword(token, NEW_PASSWORD, NEW_PASSWORD));
        }

    }

}