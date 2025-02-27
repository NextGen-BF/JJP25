package com.blankfactor.auth.controller;

import com.blankfactor.auth.entity.Role;
import com.blankfactor.auth.entity.User;
import com.blankfactor.auth.entity.dto.responses.RegisterResponse;
import com.blankfactor.auth.entity.dto.responses.VerifyResponse;
import com.blankfactor.auth.entity.dto.requests.LoginRequest;
import com.blankfactor.auth.entity.dto.requests.RegisterRequest;
import com.blankfactor.auth.entity.dto.requests.VerifyRequest;
import com.blankfactor.auth.exception.custom.code.ExpiredVerificationCodeException;
import com.blankfactor.auth.exception.custom.credentials.InvalidCredentialsException;
import com.blankfactor.auth.exception.custom.InvalidInformRequestException;
import com.blankfactor.auth.exception.custom.credentials.PasswordsDoNotMatchException;
import com.blankfactor.auth.exception.custom.ServiceUnavailableException;
import com.blankfactor.auth.exception.custom.code.IncorrectVerificationCodeException;
import com.blankfactor.auth.exception.custom.email.EmailVerificationNotFound;
import com.blankfactor.auth.exception.custom.user.*;
import com.blankfactor.auth.service.AuthService;
import com.blankfactor.auth.service.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith({MockitoExtension.class, SpringExtension.class})
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = {AuthController.class})
public class AuthControllerTests {

    private static final String REGISTER_ENDPOINT = "/api/v1/auth/register";
    private static final String VERIFY_ENDPOINT = "/api/v1/auth/verify/%s";
    private static final String RESEND_ENDPOINT = "/api/v1/auth/resend";
    private static final String LOGIN_ENDPOINT = "/api/v1/auth/login";
    private static final String RESET_PASSWORD_ENDPOINT = "/api/v1/auth/reset-password";

    private static final String TEST_EMAIL = "example@email.com";
    private static final String TEST_USERNAME = "john_doe";
    private static final String TEST_FIRST_NAME = "John";
    private static final String TEST_LAST_NAME = "Doe";
    private static final String TEST_VERIFICATION_CODE = "123456";
    private static final String TEST_VERIFYING_UUID = "45418f6e-b309-4a7e-abea-57b6fcbe18ec";

    private static final String VALID_TOKEN = "validToken";
    private static final String INVALID_TOKEN = "invalidToken";
    private static final String NEW_PASSWORD = "newPassword1!";
    private static final String DIFFERENT_PASSWORD = "differentPassword";
    private static final String PASSWORD_RESET_SUCCESS_MESSAGE = "Password has been reset successfully";
    private static final String PASSWORDS_DO_NOT_MATCH_ERROR_MESSAGE = "Passwords do not match. Please try again.";
    private static final String JWT_TOKEN_EXPIRED_ERROR_MESSAGE = "JWT token is expired or invalid";
    private static final String USER_NOT_FOUND_ERROR_MESSAGE = "User is not found";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtService jwtService;

    @Nested
    class RegisterTests {

        /* ---> TEST CASES <---
         * 1.Successful registration
         * 2.Email already in use
         * 3.Username already in use
         * 4.Passwords do not match
         * 5.Blank values (ex. "email": "")
         * 6.Plain object (ex. {}) */

        @Test
        void shouldSuccessfullySendRequestAndReceiveResponseForUser() throws Exception {
            // Given
            RegisterResponse registerResponse = RegisterResponse.builder()
                    .email(TEST_EMAIL)
                    .username(TEST_USERNAME)
                    .firstName(TEST_FIRST_NAME)
                    .lastName(TEST_LAST_NAME)
                    .enabled(false)
                    .roles(Set.of(Role.ROLE_USER.name()))
                    .build();
            String registerRequest = """
                            {
                                "email": "example@email.com",
                                "password": "Password1!",
                                "confirmPassword": "Password1!",
                                "username": "john_doe",
                                "firstName": "John",
                                "lastName": "Doe",
                                "birthDate": "2000-01-01T01:01:01",
                                "role": "attendee"
                            }
                    """;

            // When
            doReturn(registerResponse).when(authService).register(any(RegisterRequest.class));
            mockMvc.perform(post(REGISTER_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(registerRequest))

                    // Then
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").value(TEST_EMAIL))
                    .andExpect(jsonPath("$.username").value(TEST_USERNAME))
                    .andExpect(jsonPath("$.firstName").value(TEST_FIRST_NAME))
                    .andExpect(jsonPath("$.lastName").value(TEST_LAST_NAME))
                    .andExpect(jsonPath("$.enabled").value(false))
                    .andExpect(jsonPath("$.roles").isArray())
                    .andExpect(jsonPath("$.roles", hasSize(1)))
                    .andExpect(jsonPath("$.roles[0]").value(Role.ROLE_USER.name()));
        }

        @Test
        void shouldSuccessfullySendRequestAndReceiveResponseForAdmin() throws Exception {
            // Given
            RegisterResponse registerResponse = RegisterResponse.builder()
                    .email(TEST_EMAIL)
                    .username(TEST_USERNAME)
                    .firstName(TEST_FIRST_NAME)
                    .lastName(TEST_LAST_NAME)
                    .enabled(false)
                    .roles(Set.of(Role.ROLE_USER.name(), Role.ROLE_ADMIN.name()))
                    .build();
            String registerRequest = """
                            {
                                "email": "example@email.com",
                                "password": "Password1!",
                                "confirmPassword": "Password1!",
                                "username": "john_doe",
                                "firstName": "John",
                                "lastName": "Doe",
                                "birthDate": "2000-01-01T01:01:01",
                                "role": "organiser"
                            }
                    """;

            // When
            doReturn(registerResponse).when(authService).register(any(RegisterRequest.class));
            mockMvc.perform(post(REGISTER_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(registerRequest))

                    // Then
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").value(TEST_EMAIL))
                    .andExpect(jsonPath("$.username").value(TEST_USERNAME))
                    .andExpect(jsonPath("$.firstName").value(TEST_FIRST_NAME))
                    .andExpect(jsonPath("$.lastName").value(TEST_LAST_NAME))
                    .andExpect(jsonPath("$.enabled").value(false))
                    .andExpect(jsonPath("$.roles").isArray())
                    .andExpect(jsonPath("$.roles", hasSize(2)))
                    .andExpect(jsonPath("$.roles", containsInAnyOrder(Role.ROLE_USER.name(), Role.ROLE_ADMIN.name())));
        }

        @Test
        void shouldReturnResponseWithEmailIsAlreadyInUse() throws Exception {
            // Given
            String registerRequest = """
                            {
                                "email": "example@email.com",
                                "password": "Password1!",
                                "confirmPassword": "Password1!",
                                "username": "john_doe",
                                "firstName": "John",
                                "lastName": "Doe",
                                "birthDate": "2000-01-01T01:01:01",
                                "role": "attendee"
                            }
                    """;
            String ERROR_MESSAGE = TEST_EMAIL + " is already in use";

            // When
            when(authService.register(any(RegisterRequest.class))).thenThrow(new UserFoundException(ERROR_MESSAGE));
            mockMvc.perform(post(REGISTER_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(registerRequest))

                    // Then
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").value(ERROR_MESSAGE));
        }

        @Test
        void shouldReturnResponseWithUsernameIsAlreadyInUse() throws Exception {
            // Given
            String registerRequest = """
                            {
                                "email": "example@email.com",
                                "password": "Password1!",
                                "confirmPassword": "Password1!",
                                "username": "john_doe",
                                "firstName": "John",
                                "lastName": "Doe",
                                "birthDate": "2000-01-01T01:01:01",
                                "role": "attendee"
                            }
                    """;
            String ERROR_MESSAGE = TEST_USERNAME + " is already in use";

            // When
            when(authService.register(any(RegisterRequest.class))).thenThrow(new UserFoundException(ERROR_MESSAGE));
            mockMvc.perform(post(REGISTER_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(registerRequest))

                    // Then
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").value(ERROR_MESSAGE));
        }

        @Test
        void shouldReturnResponseWithPasswordsDoNotMatchError() throws Exception {
            // Given
            String registerRequest = """
                            {
                                "email": "example@email.com",
                                "password": "Password1!",
                                "confirmPassword": "Password2!",
                                "username": "john_doe",
                                "firstName": "John",
                                "lastName": "Doe",
                                "birthDate": "2000-01-01T01:01:01",
                                "role": "attendee"
                            }
                    """;
            String ERROR_MESSAGE = "Passwords do not match. Please try again.";

            // When
            when(authService.register(any(RegisterRequest.class))).thenThrow(new PasswordsDoNotMatchException(ERROR_MESSAGE));
            mockMvc.perform(post(REGISTER_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(registerRequest))

                    // Then
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(ERROR_MESSAGE));
        }

        @Test
        void shouldReturnResponseIncludingAllFieldErrors() throws Exception {
            // Given
            String registerRequest = """
                            {
                              "email": "",
                              "password": "",
                              "confirmPassword": "",
                              "username": "",
                              "firstName": "",
                              "lastName": "",
                              "birthDate": "",
                              "role": ""
                            }
                    """;

            // When
            mockMvc.perform(post(REGISTER_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(registerRequest))

                    // Then
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.email").isArray())
                    .andExpect(jsonPath("$.email", hasSize(1)))
                    .andExpect(jsonPath("$.password").isArray())
                    .andExpect(jsonPath("$.password", hasSize(1)))
                    .andExpect(jsonPath("$.confirmPassword").isArray())
                    .andExpect(jsonPath("$.confirmPassword", hasSize(1)))
                    .andExpect(jsonPath("$.username").isArray())
                    .andExpect(jsonPath("$.username", hasSize(1)))
                    .andExpect(jsonPath("$.firstName").isArray())
                    .andExpect(jsonPath("$.firstName", hasSize(1)))
                    .andExpect(jsonPath("$.lastName").isArray())
                    .andExpect(jsonPath("$.lastName", hasSize(1)))
                    .andExpect(jsonPath("$.birthDate").isArray())
                    .andExpect(jsonPath("$.birthDate", hasSize(1)))
                    .andExpect(jsonPath("$.role").isArray())
                    .andExpect(jsonPath("$.role", hasSize(1)));
        }

        @Test
        void shouldReturnResponseWithOnlyRequiredFieldsErrors() throws Exception {
            // When
            mockMvc.perform(post(REGISTER_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content("{}"))

                    // Then
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.email").isArray())
                    .andExpect(jsonPath("$.email", hasSize(1)))
                    .andExpect(jsonPath("$.password").isArray())
                    .andExpect(jsonPath("$.password", hasSize(1)))
                    .andExpect(jsonPath("$.confirmPassword").isArray())
                    .andExpect(jsonPath("$.confirmPassword", hasSize(1)))
                    .andExpect(jsonPath("$.username").isArray())
                    .andExpect(jsonPath("$.username", hasSize(1)))
                    .andExpect(jsonPath("$.firstName").isArray())
                    .andExpect(jsonPath("$.firstName", hasSize(1)))
                    .andExpect(jsonPath("$.lastName").isArray())
                    .andExpect(jsonPath("$.lastName", hasSize(1)))
                    .andExpect(jsonPath("$.birthDate").isArray())
                    .andExpect(jsonPath("$.birthDate", hasSize(1)))
                    .andExpect(jsonPath("$.role").isArray())
                    .andExpect(jsonPath("$.role", hasSize(1)));
        }
    }

    @Nested
    class VerifyTests {

        /* ---> TEST CASES <---
         * 1.Successful verification
         * 2.Email verification id not found
         * 3.User already verified
         * 4.Incorrect verification code
         * 5.Expired verification code
         * 6.Blank values (ex. "email": "")
         * 7.Plain object (ex. {})
         * 8.Method informEmsApp() throws Forbidden
         * 9.Method informEmsApp() throws Conflict
         * 10.Verification not possible */

        @Test
        void shouldSuccessfullyVerifyUser() throws Exception {
            // Given
            String verifyRequest = """
                        {
                            "code": "123123"
                        }
                    """;
            VerifyResponse verifyResponse = new VerifyResponse(TEST_EMAIL, true);

            // When
            when(authService.verify(eq(TEST_VERIFYING_UUID), any(VerifyRequest.class))).thenReturn(verifyResponse);
            mockMvc.perform(post(String.format(VERIFY_ENDPOINT, TEST_VERIFYING_UUID)).contentType(MediaType.APPLICATION_JSON).content(verifyRequest))

                    // Then
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").value(TEST_EMAIL))
                    .andExpect(jsonPath("$.enabled").value(true));
        }

        @Test
        void shouldReturnResponseWithEmailVerificationNotFoundError() throws Exception {
            // Given
            String verifyRequest = """
                        {
                            "code": "123123"
                        }
                    """;
            String ERROR_MESSAGE = TEST_VERIFYING_UUID + " is not found";

            // When
            when(authService.verify(eq(TEST_VERIFYING_UUID), any(VerifyRequest.class))).thenThrow(new EmailVerificationNotFound(ERROR_MESSAGE));
            mockMvc.perform(post(String.format(VERIFY_ENDPOINT, TEST_VERIFYING_UUID)).contentType(MediaType.APPLICATION_JSON).content(verifyRequest))

                    // Then
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value(ERROR_MESSAGE));
        }

        @Test
        void shouldReturnResponseWithUserIsAlreadyVerifiedError() throws Exception {
            // Given
            String verifyRequest = """
                    {
                        "code": "123123"
                    }
                    """;
            String ERROR_MESSAGE = TEST_EMAIL + " is already verified";

            // When
            when(authService.verify(eq(TEST_VERIFYING_UUID), any(VerifyRequest.class))).thenThrow(new UserVerifiedException(ERROR_MESSAGE));
            mockMvc.perform(post(String.format(VERIFY_ENDPOINT, TEST_VERIFYING_UUID)).contentType(MediaType.APPLICATION_JSON).content(verifyRequest))

                    // Then
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").value(ERROR_MESSAGE));
        }

        @Test
        void shouldReturnResponseWithIncorrectCodeError() throws Exception {
            // Given
            String verifyRequest = """
                    {
                        "code": "123456"
                    }
                    """;
            String ERROR_MESSAGE = "Incorrect verification code: " + TEST_VERIFICATION_CODE;

            // When
            when(authService.verify(eq(TEST_VERIFYING_UUID), any(VerifyRequest.class))).thenThrow(new IncorrectVerificationCodeException(ERROR_MESSAGE));
            mockMvc.perform(post(String.format(VERIFY_ENDPOINT, TEST_VERIFYING_UUID)).contentType(MediaType.APPLICATION_JSON).content(verifyRequest))

                    // Then
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(ERROR_MESSAGE));
        }

        @Test
        void shouldReturnResponseWithExpiredCodeError() throws Exception {
            // Given
            String verifyRequest = """
                    {
                        "code": "123456"
                    }
                    """;
            String ERROR_MESSAGE = "Verification code " + TEST_VERIFICATION_CODE + " has expired.";

            // When
            when(authService.verify(eq(TEST_VERIFYING_UUID), any(VerifyRequest.class))).thenThrow(new ExpiredVerificationCodeException(ERROR_MESSAGE));
            mockMvc.perform(post(String.format(VERIFY_ENDPOINT, TEST_VERIFYING_UUID)).contentType(MediaType.APPLICATION_JSON).content(verifyRequest))

                    // Then
                    .andExpect(status().isGone())
                    .andExpect(jsonPath("$.message").value(ERROR_MESSAGE));
        }

        @Test
        void shouldReturnResponseIncludingAllFieldErrors() throws Exception {
            // Given
            String verifyRequest = """
                            {
                                "code": ""
                            }
                    """;

            // When
            mockMvc.perform(post(String.format(VERIFY_ENDPOINT, TEST_VERIFYING_UUID)).contentType(MediaType.APPLICATION_JSON).content(verifyRequest))

                    // Then
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").isArray())
                    .andExpect(jsonPath("$.code", hasSize(1)));
        }

        @Test
        void shouldReturnResponseWithOnlyRequiredFieldsErrors() throws Exception {
            // When
            mockMvc.perform(post(String.format(VERIFY_ENDPOINT, TEST_VERIFYING_UUID)).contentType(MediaType.APPLICATION_JSON).content("{}"))

                    // Then
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").isArray())
                    .andExpect(jsonPath("$.code", hasSize(1)));
        }

        @Test
        void shouldReturnResponseWithInvalidInformRequestError() throws Exception {
            // Given
            String verifyRequest = """
                    {
                        "code": "123456"
                    }
                    """;
            String ERROR_MESSAGE = "The request might not have token or has an expired one";

            // When
            when(authService.verify(eq(TEST_VERIFYING_UUID), any(VerifyRequest.class))).thenThrow(new InvalidInformRequestException(ERROR_MESSAGE));
            mockMvc.perform(post(String.format(VERIFY_ENDPOINT, TEST_VERIFYING_UUID)).contentType(MediaType.APPLICATION_JSON).content(verifyRequest))

                    // Then
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.message").value(ERROR_MESSAGE));
        }

        @Test
        void shouldReturnResponseWithUserExistsError() throws Exception {
            // Given
            String verifyRequest = """
                    {
                        "code": "123456"
                    }
                    """;
            String ERROR_MESSAGE = "User with id: 1 already exists";

            // When
            when(authService.verify(eq(TEST_VERIFYING_UUID), any(VerifyRequest.class))).thenThrow(new UserExistsException(ERROR_MESSAGE));
            mockMvc.perform(post(String.format(VERIFY_ENDPOINT, TEST_VERIFYING_UUID)).contentType(MediaType.APPLICATION_JSON).content(verifyRequest))

                    // Then
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").value(ERROR_MESSAGE));
        }

        @Test
        void shouldReturnResponseWithServiceUnavailableError() throws Exception {
            // Given
            String verifyRequest = """
                    {
                        "code": "123456"
                    }
                    """;
            String ERROR_MESSAGE = "Sorry, verification is not possible at the moment.";

            // When
            when(authService.verify(eq(TEST_VERIFYING_UUID), any(VerifyRequest.class))).thenThrow(new ServiceUnavailableException(ERROR_MESSAGE));
            mockMvc.perform(post(String.format(VERIFY_ENDPOINT, TEST_VERIFYING_UUID)).contentType(MediaType.APPLICATION_JSON).content(verifyRequest))

                    // Then
                    .andExpect(status().isServiceUnavailable())
                    .andExpect(jsonPath("$.message").value(ERROR_MESSAGE));
        }

    }

    @Nested
    class ResendTests {

        /* ---> TEST CASES <---
         * 1.Successful resending
         * 2.User not found
         * 3.User already verified
         * 4.Invalid request parameter (ex. ?email=)
         * 5.Missing request parameter */

        @Test
        void shouldSuccessfullyResendVerificationEmail() throws Exception {
            // When
            when(authService.resend(any(String.class))).thenReturn(TEST_VERIFICATION_CODE);
            mockMvc.perform(post(RESEND_ENDPOINT + "?email=" + TEST_EMAIL))

                    // Then
                    .andExpect(status().isOk())
                    .andExpect(content().string("Verification code resent successfully!"));
        }

        @Test
        void shouldReturnResponseWithUserNotFoundError() throws Exception {
            // Given
            String ERROR_MESSAGE = TEST_EMAIL + " is not found";

            // When
            when(authService.resend(any(String.class))).thenThrow(new UserNotFoundException(ERROR_MESSAGE));
            mockMvc.perform(post(RESEND_ENDPOINT + "?email=" + TEST_EMAIL))

                    // Then
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value(ERROR_MESSAGE));
        }

        @Test
        void shouldReturnResponseWithUserAlreadyVerifiedError() throws Exception {
            // Given
            String ERROR_MESSAGE = TEST_EMAIL + " is already verified";

            // When
            when(authService.resend(any(String.class))).thenThrow(new UserVerifiedException(ERROR_MESSAGE));
            mockMvc.perform(post(RESEND_ENDPOINT + "?email=" + TEST_EMAIL))

                    // Then
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").value(ERROR_MESSAGE));
        }

        @Test
        void shouldReturnResponseIncludingAllFieldErrors() throws Exception {/*TODO*/}

        @Test
        void shouldReturnResponseWithOnlyRequiredFieldsErrors() throws Exception {/*TODO*/}

    }

    @Nested
    class LoginTests {

        @Test
        void shouldReturnLoginResponseForValidCredentials() throws Exception {
            String loginRequestJson = """
                    {
                        "loginIdentifier": "test@example.com",
                        "password": "password"
                    }
                    """;

            User user = User.builder()
                    .id(1L)
                    .email("test@example.com")
                    .username("testuser")
                    .password("encodedPassword")
                    .enabled(true)
                    .build();

            String jwtToken = "dummy.jwt.token";
            long expirationTime = 3600000L;

            when(authService.login(any(LoginRequest.class))).thenReturn(user);
            when(jwtService.generateToken(any(User.class))).thenReturn(jwtToken);
            when(jwtService.getExpirationTime()).thenReturn(expirationTime);

            mockMvc.perform(post(LOGIN_ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(loginRequestJson))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").value(jwtToken))
                    .andExpect(jsonPath("$.expiresIn").value(expirationTime));
        }

        @Test
        void shouldReturnNotFoundForNonExistentUser() throws Exception {
            String loginRequestJson = """
                    {
                        "loginIdentifier": "nonexistent@example.com",
                        "password": "password"
                    }
                    """;
            String errorMessage = "nonexistent@example.com is not found";

            when(authService.login(any(LoginRequest.class))).thenThrow(new UserNotFoundException(errorMessage));

            mockMvc.perform(post(LOGIN_ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(loginRequestJson))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value(errorMessage));
        }

        @Test
        void shouldReturnUnauthorizedForInvalidPassword() throws Exception {
            String loginRequestJson = """
                    {
                        "loginIdentifier": "test@example.com",
                        "password": "wrongPassword"
                    }
                    """;
            String errorMessage = "Incorrect password.";

            when(authService.login(any(LoginRequest.class))).thenThrow(new InvalidCredentialsException(errorMessage));

            mockMvc.perform(post(LOGIN_ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(loginRequestJson))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message").value(errorMessage));
        }

        @Test
        void shouldReturnForbiddenForUnverifiedUser() throws Exception {
            String loginRequestJson = """
                    {
                        "loginIdentifier": "test@example.com",
                        "password": "password"
                    }
                    """;
            String errorMessage = "Account is not verified!";

            when(authService.login(any(LoginRequest.class))).thenThrow(new UserNotVerifiedException(errorMessage));

            mockMvc.perform(post(LOGIN_ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(loginRequestJson))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.message").value(errorMessage));
        }

        @Test
        void shouldReturnBadRequestForEmptyLoginIdentifier() throws Exception {
            String loginRequestJson = """
                    {
                        "loginIdentifier": "",
                        "password": "password"
                    }
                    """;
            String errorMessage = "Login identifier cannot be null or empty";

            when(authService.login(any(LoginRequest.class))).thenThrow(new IllegalArgumentException(errorMessage));

            mockMvc.perform(post(LOGIN_ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(loginRequestJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString(errorMessage)));
        }
    }

    @Nested
    class ResetPasswordTests {

        @Test
        void shouldSuccessfullyResetPassword() throws Exception {
            String requestBody = String.format("""
                    {
                        "token": "%s",
                        "newPassword": "%s",
                        "confirmPassword": "%s"
                    }
                    """, VALID_TOKEN, NEW_PASSWORD, NEW_PASSWORD);

            doNothing().when(authService).resetPassword(VALID_TOKEN, NEW_PASSWORD, NEW_PASSWORD);
            mockMvc.perform(post(RESET_PASSWORD_ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isOk())
                    .andExpect(content().string(PASSWORD_RESET_SUCCESS_MESSAGE));
        }

        @Test
        void shouldReturnBadRequestWhenPasswordsDoNotMatch() throws Exception {
            String requestBody = String.format("""
                    {
                        "token": "%s",
                        "newPassword": "%s",
                        "confirmPassword": "%s"
                    }
                    """, VALID_TOKEN, NEW_PASSWORD, DIFFERENT_PASSWORD);

            doThrow(new PasswordsDoNotMatchException(PASSWORDS_DO_NOT_MATCH_ERROR_MESSAGE))
                    .when(authService).resetPassword(VALID_TOKEN, NEW_PASSWORD, DIFFERENT_PASSWORD);

            mockMvc.perform(post(RESET_PASSWORD_ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(PASSWORDS_DO_NOT_MATCH_ERROR_MESSAGE));
        }

        @Test
        void shouldReturnUnauthorizedWhenTokenIsInvalid() throws Exception {
            String requestBody = String.format("""
                    {
                        "token": "%s",
                        "newPassword": "%s",
                        "confirmPassword": "%s"
                    }
                    """, INVALID_TOKEN, NEW_PASSWORD, NEW_PASSWORD);

            doThrow(new ExpiredJwtException(null, null, JWT_TOKEN_EXPIRED_ERROR_MESSAGE))
                    .when(authService).resetPassword(INVALID_TOKEN, NEW_PASSWORD, NEW_PASSWORD);

            mockMvc.perform(post(RESET_PASSWORD_ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message").value(JWT_TOKEN_EXPIRED_ERROR_MESSAGE));
        }

        @Test
        void shouldReturnNotFoundWhenUserIsNotFound() throws Exception {
            String requestBody = String.format("""
                    {
                        "token": "%s",
                        "newPassword": "%s",
                        "confirmPassword": "%s"
                    }
                    """, VALID_TOKEN, NEW_PASSWORD, NEW_PASSWORD);


            doThrow(new UserNotFoundException(USER_NOT_FOUND_ERROR_MESSAGE))
                    .when(authService).resetPassword(VALID_TOKEN, NEW_PASSWORD, NEW_PASSWORD);

            mockMvc.perform(post(RESET_PASSWORD_ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value(USER_NOT_FOUND_ERROR_MESSAGE));
        }
    }

}
