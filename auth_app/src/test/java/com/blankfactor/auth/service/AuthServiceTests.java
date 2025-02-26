package com.blankfactor.auth.service;

import com.blankfactor.auth.entity.Role;
import com.blankfactor.auth.entity.User;
import com.blankfactor.auth.entity.dto.outgoing.RegisterResponse;
import com.blankfactor.auth.entity.dto.incoming.LoginRequest;
import com.blankfactor.auth.entity.dto.incoming.RegisterRequest;
import com.blankfactor.auth.entity.dto.incoming.VerifyRequest;
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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.thymeleaf.TemplateEngine;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTests {

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

    private static final String TEST_EMAIL = "example@email.com";
    private static final String TEST_PASSWORD = "Password1!";
    private static final String TEST_USERNAME = "john_doe";
    private static final String TEST_FIRST_NAME = "John";
    private static final String TEST_LAST_NAME = "Doe";
    private static final String TEST_BIRTHDATE = "2000-01-01T01:01:01";
    private static final String TEST_ATTENDEE_ROLE = "attendee";
    private static final String TEST_VERIFICATION_CODE = "123456";
    private static final String VALID_TOKEN = "validToken";
    private static final String NEW_PASSWORD = "newPassword1!";
    private static final String DIFFERENT_PASSWORD = "differentPassword";

    @Nested
    class ValidateCredentialsTests {
        @Test
        void shouldThrowUserFoundExceptionWhenUserWithSuchEmailExists() {
            // Given
            RegisterRequest registerRequest = RegisterRequest.builder()
                    .email(TEST_EMAIL)
                    .username(TEST_USERNAME)
                    .build();

            // When
            User existingUser = User.builder()
                    .email(registerRequest.getEmail())
                    .build();
            when(userRepository.findByEmail(registerRequest.getEmail())).thenReturn(Optional.of(existingUser));

            // Then
            UserFoundException exception = assertThrows(UserFoundException.class, () -> authService.validateCredentials(registerRequest));
            assertEquals(TEST_EMAIL + " is already in use", exception.getMessage());
        }

        @Test
        void shouldThrowUserFoundExceptionWhenUserWithSuchUsernameExists() {
            // Given
            RegisterRequest registerRequest = RegisterRequest.builder()
                    .email(TEST_EMAIL)
                    .username(TEST_USERNAME)
                    .build();
            User existingUser = User.builder()
                    .username(registerRequest.getUsername())
                    .build();

            // When
            when(userRepository.findByEmail(registerRequest.getEmail())).thenReturn(Optional.empty());
            when(userRepository.findByUsername(registerRequest.getUsername())).thenReturn(Optional.of(existingUser));

            // Then
            UserFoundException exception = assertThrows(UserFoundException.class, () -> authService.validateCredentials(registerRequest));
            assertEquals(TEST_USERNAME + " is already in use", exception.getMessage());
        }

        @Test
        void shouldThrowPasswordsDoNotMatchExceptionWhenThePasswordsDoNotMatch() {
            // Given
            RegisterRequest registerRequest = RegisterRequest.builder()
                    .email(TEST_EMAIL)
                    .username(TEST_USERNAME)
                    .password(TEST_PASSWORD)
                    .confirmPassword("notMatchingPassword").build();

            // When
            when(userRepository.findByEmail(registerRequest.getEmail())).thenReturn(Optional.empty());
            when(userRepository.findByUsername(registerRequest.getUsername())).thenReturn(Optional.empty());

            // Then
            PasswordsDoNotMatchException exception = assertThrows(PasswordsDoNotMatchException.class, () -> authService.validateCredentials(registerRequest));
            assertEquals("Passwords do not match. Please try again.", exception.getMessage());
        }
    }

    @Nested
    class RegisterTests {
        @Test
        void shouldSuccessfullyRegisterUser() throws MessagingException {
            // Given
            RegisterRequest registerRequest = RegisterRequest.builder()
                    .email(TEST_EMAIL)
                    .password(TEST_PASSWORD)
                    .confirmPassword(TEST_PASSWORD)
                    .firstName(TEST_FIRST_NAME)
                    .lastName(TEST_LAST_NAME)
                    .birthDate(LocalDateTime.parse(TEST_BIRTHDATE))
                    .role(TEST_ATTENDEE_ROLE)
                    .build();

            // When
            when(userRepository.findByEmail(registerRequest.getEmail())).thenReturn(Optional.empty());
            when(userRepository.findByUsername(registerRequest.getUsername())).thenReturn(Optional.empty());
            when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
            when(modelMapper.map(any(User.class), eq(RegisterResponse.class))).thenReturn(new RegisterResponse());
            when(templateEngine.process(anyString(), any())).thenReturn("Mocked Email Content");
            RegisterResponse registerResponse = authService.register(registerRequest);

            // Then
            verify(userRepository).saveAndFlush(any(User.class));
            verify(emailService).sendVerificationEmail(eq(registerRequest.getEmail()), eq("Account Verification"), eq("Mocked Email Content"));
            assertNotNull(registerResponse);
        }

        @Test
        void shouldThrowVerificationEmailNotSentExceptionWhenEmailIsNotSent() throws MessagingException {
            // Given
            RegisterRequest registerRequest = RegisterRequest.builder()
                    .email(TEST_EMAIL)
                    .password(TEST_PASSWORD)
                    .confirmPassword(TEST_PASSWORD)
                    .username(TEST_USERNAME)
                    .firstName(TEST_FIRST_NAME)
                    .lastName(TEST_LAST_NAME)
                    .birthDate(LocalDateTime.parse(TEST_BIRTHDATE))
                    .role(TEST_ATTENDEE_ROLE).build();

            // When
            when(userRepository.findByEmail(registerRequest.getEmail())).thenReturn(Optional.empty());
            when(userRepository.findByUsername(registerRequest.getUsername())).thenReturn(Optional.empty());
            when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
            when(templateEngine.process(anyString(), any())).thenReturn("Mocked Email Content");
            doThrow(new MessagingException("Failed to send email")).when(emailService).sendVerificationEmail(anyString(), anyString(), anyString());

            // Then
            VerificationEmailNotSentException exception = assertThrows(VerificationEmailNotSentException.class, () -> authService.register(registerRequest));
            assertEquals("Failed to send verification email to " + TEST_EMAIL, exception.getMessage());
        }
    }

    @Nested
    class VerifyTests {
        @Test
        void shouldThrowUserNotFoundExceptionWhenUserWithSuchEmailDoesNotExits() {
            // Given
            VerifyRequest verifyRequest = VerifyRequest.builder()
                    .email(TEST_EMAIL)
                    .verificationCode(TEST_VERIFICATION_CODE)
                    .build();

            // When
            when(userRepository.findByEmail(verifyRequest.getEmail())).thenReturn(Optional.empty());

            // Then
            UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> authService.verify(verifyRequest));
            assertEquals(TEST_EMAIL + " is not found", exception.getMessage());
        }

        @Test
        void shouldThrowUserVerifiedExceptionWhenTheUserIsVerified() {
            // Given
            VerifyRequest verifyRequest = VerifyRequest.builder()
                    .email(TEST_EMAIL)
                    .verificationCode(TEST_VERIFICATION_CODE)
                    .build();
            User user = User.builder()
                    .enabled(true)
                    .build();

            // When
            when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));

            // Then
            UserVerifiedException exception = assertThrows(UserVerifiedException.class, () -> authService.verify(verifyRequest));
            assertEquals(TEST_EMAIL + " is already verified", exception.getMessage());
        }

        @Test
        void shouldThrowIncorrectVerificationCodeExceptionWhenTheCodeIsIncorrect() {
            // Given
            VerifyRequest verifyRequest = VerifyRequest.builder()
                    .email(TEST_EMAIL)
                    .verificationCode(TEST_VERIFICATION_CODE)
                    .build();
            User user = User.builder()
                    .enabled(false)
                    .verificationCode("654321")
                    .build();

            // When
            when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));

            // Then
            IncorrectVerificationCodeException exception = assertThrows(IncorrectVerificationCodeException.class, () -> authService.verify(verifyRequest));
            assertEquals("Incorrect verification code: " + TEST_VERIFICATION_CODE, exception.getMessage());
        }

        @Test
        void shouldThrowExpiredVerificationCodeExceptionWhenTheCodeHasExpired() {
            // Given
            VerifyRequest verifyRequest = VerifyRequest.builder()
                    .email(TEST_EMAIL)
                    .verificationCode(TEST_VERIFICATION_CODE)
                    .build();
            User user = User.builder()
                    .enabled(false)
                    .verificationCode(TEST_VERIFICATION_CODE)
                    .verificationCodeExpiresAt(LocalDateTime.now().minusMinutes(1))
                    .build();

            // When
            when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));

            // Then
            ExpiredVerificationCodeException exception = assertThrows(ExpiredVerificationCodeException.class, () -> authService.verify(verifyRequest));
            assertEquals("Verification code " + TEST_VERIFICATION_CODE + " has expired", exception.getMessage());
        }

        @Test
        void shouldSuccessfullyVerifyUser() {
            // Given
            VerifyRequest verifyRequest = VerifyRequest.builder()
                    .email(TEST_EMAIL)
                    .verificationCode(TEST_VERIFICATION_CODE)
                    .build();
            User user = User.builder()
                    .enabled(false)
                    .verificationCode(TEST_VERIFICATION_CODE)
                    .verificationCodeExpiresAt(LocalDateTime.now().plusMinutes(10))
                    .build();

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
            VerifyRequest verifyRequest = VerifyRequest.builder()
                    .email(TEST_EMAIL)
                    .verificationCode(TEST_VERIFICATION_CODE)
                    .build();
            User user = User.builder()
                    .email(TEST_EMAIL)
                    .enabled(false)
                    .verificationCode(TEST_VERIFICATION_CODE)
                    .verificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15))
                    .roles(Set.of(Role.ROLE_USER))
                    .build();

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
            VerifyRequest verifyRequest = VerifyRequest.builder()
                    .email(TEST_EMAIL)
                    .verificationCode(TEST_VERIFICATION_CODE)
                    .build();
            User user = User.builder()
                    .email(TEST_EMAIL)
                    .enabled(false)
                    .verificationCode(TEST_VERIFICATION_CODE)
                    .verificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15))
                    .roles(Set.of(Role.ROLE_ADMIN, Role.ROLE_USER))
                    .build();

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
            VerifyRequest verifyRequest = VerifyRequest.builder()
                    .email(TEST_EMAIL)
                    .verificationCode(TEST_VERIFICATION_CODE)
                    .build();
            User user = User.builder()
                    .email(TEST_EMAIL)
                    .enabled(false)
                    .verificationCode(TEST_VERIFICATION_CODE)
                    .verificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15))
                    .roles(Set.of(Role.ROLE_ADMIN, Role.ROLE_USER))
                    .build();

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
            User user = User.builder()
                    .email(TEST_EMAIL)
                    .enabled(true)
                    .build();
            // When
            when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));

            // Then
            UserVerifiedException exception = assertThrows(UserVerifiedException.class, () -> authService.resend(TEST_EMAIL));
            assertEquals(TEST_EMAIL + " is already verified", exception.getMessage());
        }

        @Test
        void shouldSuccessfullyResendVerificationCode() throws MessagingException {
            // Given
            User user = User.builder()
                    .email(TEST_EMAIL)
                    .enabled(false)
                    .build();

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
            // Given
            LoginRequest loginRequest = LoginRequest.builder()
                    .loginIdentifier(TEST_EMAIL)
                    .password(TEST_PASSWORD)
                    .build();

            User user = User.builder()
                    .id(1L)
                    .email(TEST_EMAIL)
                    .username(TEST_USERNAME)
                    .password("encodedPassword")
                    .enabled(true)
                    .build();

            // When
            when(userRepository.findByEmailOrUsername(TEST_EMAIL)).thenReturn(Optional.of(user));
            User result = authService.login(loginRequest);

            // Then
            assertNotNull(result);
            assertEquals(user.getId(), result.getId());
            assertEquals(user.getEmail(), result.getEmail());
        }

        @Test
        void loginWithValidUsernameReturnsUser() {
            // Given
            LoginRequest loginRequest = LoginRequest.builder()
                    .loginIdentifier(TEST_USERNAME)
                    .password(TEST_PASSWORD)
                    .build();

            User user = User.builder()
                    .id(1L)
                    .email(TEST_EMAIL)
                    .username(TEST_USERNAME)
                    .password("encodedPassword")
                    .enabled(true)
                    .build();

            // When
            when(userRepository.findByEmailOrUsername(TEST_USERNAME)).thenReturn(Optional.of(user));
            User result = authService.login(loginRequest);

            // Then
            assertNotNull(result);
            assertEquals(user.getId(), result.getId());
            assertEquals(user.getEmail(), result.getEmail());
        }

        @Test
        void loginWithNonExistentUserThrowsInvalidCredentialsException() {
            // Given
            LoginRequest loginRequest = LoginRequest.builder()
                    .loginIdentifier(TEST_EMAIL)
                    .password(TEST_PASSWORD)
                    .build();

            // When
            when(userRepository.findByEmailOrUsername(TEST_EMAIL)).thenReturn(Optional.empty());

            // Then
            assertThrows(InvalidCredentialsException.class, () -> authService.login(loginRequest));
        }

        @Test
        void loginWithUnverifiedUserThrowsUserNotVerifiedException() {
            // Given
            LoginRequest loginRequest = LoginRequest.builder()
                    .loginIdentifier(TEST_EMAIL)
                    .password(TEST_PASSWORD)
                    .build();

            User user = User.builder()
                    .id(2L)
                    .email(TEST_EMAIL)
                    .username(TEST_USERNAME)
                    .password("encodedPassword")
                    .enabled(false)
                    .build();

            // When
            when(userRepository.findByEmailOrUsername(TEST_EMAIL)).thenReturn(Optional.of(user));

            // Then
            Exception exception = assertThrows(UserNotVerifiedException.class, () -> authService.login(loginRequest));
            assertTrue(exception.getMessage().contains("Account is not verified"));
        }

        @Test
        void loginWithInvalidCredentialsThrowsInvalidPasswordException() {
            // Given
            LoginRequest loginRequest = LoginRequest.builder()
                    .loginIdentifier(TEST_EMAIL)
                    .password(TEST_PASSWORD)
                    .build();
            loginRequest.setLoginIdentifier(TEST_EMAIL);

            User user = User.builder()
                    .id(3L)
                    .email(TEST_EMAIL)
                    .username(TEST_USERNAME)
                    .password("encodedPassword")
                    .enabled(true)
                    .build();

            // When
            when(userRepository.findByEmailOrUsername(TEST_EMAIL)).thenReturn(Optional.of(user));
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenThrow(new BadCredentialsException("Bad credentials"));

            // Then
            assertThrows(InvalidCredentialsException.class, () -> authService.login(loginRequest));
        }

        @Test
        void loginWithNullIdentifierThrowsIllegalArgumentException() {
            // Given
            LoginRequest loginRequest = LoginRequest.builder()
                    .loginIdentifier(null)
                    .password(TEST_PASSWORD)
                    .build();

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> authService.login(loginRequest));
        }

        @Test
        void loginWithEmptyIdentifierThrowsIllegalArgumentException() {
            // Given
            LoginRequest loginRequest = LoginRequest.builder()
                    .loginIdentifier("")
                    .password(TEST_PASSWORD)
                    .build();

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> authService.login(loginRequest));
        }

        @Test
        void loginWithBlankIdentifierThrowsIllegalArgumentException() {
            // Given
            LoginRequest loginRequest = LoginRequest.builder()
                    .loginIdentifier(" ")
                    .password(TEST_PASSWORD)
                    .build();

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> authService.login(loginRequest));
        }
    }

    @Nested
    class ResetPasswordTests {

        @Test
        void shouldSuccessfullyResetPassword() {
            // Given
            User user = User.builder().username(TEST_USERNAME).build();

            // When
            when(jwtService.extractUsername(VALID_TOKEN)).thenReturn(TEST_USERNAME);
            when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(user));
            authService.resetPassword(VALID_TOKEN, NEW_PASSWORD, NEW_PASSWORD);

            // Then
            verify(passwordEncoder).encode(NEW_PASSWORD);
            verify(userRepository).saveAndFlush(user);
            assertEquals(passwordEncoder.encode(NEW_PASSWORD), user.getPassword());
        }

        @Test
        void shouldThrowPasswordsDoNotMatchExceptionWhenPasswordsDoNotMatch() {
            // Given & When & Then
            assertThrows(PasswordsDoNotMatchException.class,
                    () -> authService.resetPassword(VALID_TOKEN, NEW_PASSWORD, DIFFERENT_PASSWORD));
        }

        @Test
        void shouldThrowUserNotFoundExceptionWhenUserIsNotFound() {
            // When
            when(jwtService.extractUsername(VALID_TOKEN)).thenReturn(TEST_USERNAME);
            when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.empty());

            // Then
            assertThrows(UserNotFoundException.class,
                    () -> authService.resetPassword(VALID_TOKEN, NEW_PASSWORD, NEW_PASSWORD));
        }

    }

}