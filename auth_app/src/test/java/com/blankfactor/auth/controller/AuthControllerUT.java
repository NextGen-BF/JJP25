package com.blankfactor.auth.controller;

import com.blankfactor.auth.entity.User;
import com.blankfactor.auth.entity.dto.exp.RegisterResponse;
import com.blankfactor.auth.entity.dto.exp.VerifyResponse;
import com.blankfactor.auth.entity.dto.imp.LoginRequest;
import com.blankfactor.auth.entity.dto.imp.RegisterRequest;
import com.blankfactor.auth.entity.dto.imp.VerifyRequest;
import com.blankfactor.auth.exception.custom.InvalidCredentialsException;
import com.blankfactor.auth.exception.custom.PasswordsDoNotMatchException;
import com.blankfactor.auth.exception.custom.code.IncorrectVerificationCodeException;
import com.blankfactor.auth.exception.custom.user.UserFoundException;
import com.blankfactor.auth.exception.custom.user.UserNotFoundException;
import com.blankfactor.auth.exception.custom.user.UserNotVerifiedException;
import com.blankfactor.auth.exception.custom.user.UserVerifiedException;
import com.blankfactor.auth.service.AuthService;
import com.blankfactor.auth.service.JwtService;
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
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith({MockitoExtension.class, SpringExtension.class})
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = {AuthController.class})
public class AuthControllerUT {

    private static final String REGISTER_ENDPOINT = "/api/v1/auth/register";
    private static final String VERIFY_ENDPOINT = "/api/v1/auth/verify";
    private static final String RESEND_ENDPOINT = "/api/v1/auth/resend";
    private static final String LOGIN_ENDPOINT = "/api/v1/auth/login";

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
            RegisterResponse registerResponse = new RegisterResponse(
                    "example@email.com",
                    "username",
                    "firstName",
                    "lastName",
                    false,
                    Set.of("ROLE_USER"));
            String registerRequest = """
                            {
                                "email": "example@email.com",
                                "password": "Password1!",
                                "confirmPassword": "Password1!",
                                "username": "username",
                                "firstName": "firstName",
                                "lastName": "lastName",
                                "birthDate": "2000-01-01T01:01:01",
                                "role": "attendee"
                            }
                    """;

            // When
            doReturn(registerResponse).when(authService).register(any(RegisterRequest.class));
            mockMvc.perform(post(REGISTER_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(registerRequest))

                    // Then
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").value("example@email.com"))
                    .andExpect(jsonPath("$.username").value("username"))
                    .andExpect(jsonPath("$.firstName").value("firstName"))
                    .andExpect(jsonPath("$.lastName").value("lastName"))
                    .andExpect(jsonPath("$.enabled").value(false))
                    .andExpect(jsonPath("$.roles").isArray())
                    .andExpect(jsonPath("$.roles", hasSize(1)))
                    .andExpect(jsonPath("$.roles[0]").value("ROLE_USER"));
        }

        @Test
        void shouldSuccessfullySendRequestAndReceiveResponseForAdmin() throws Exception {
            // Given
            RegisterResponse registerResponse = new RegisterResponse(
                    "example@email.com",
                    "username",
                    "firstName",
                    "lastName",
                    false,
                    Set.of("ROLE_USER", "ROLE_ADMIN"));
            String registerRequest = """
                            {
                                "email": "example@email.com",
                                "password": "Password1!",
                                "confirmPassword": "Password1!",
                                "username": "username",
                                "firstName": "firstName",
                                "lastName": "lastName",
                                "birthDate": "2000-01-01T01:01:01",
                                "role": "organiser"
                            }
                    """;

            // When
            doReturn(registerResponse).when(authService).register(any(RegisterRequest.class));
            mockMvc.perform(post(REGISTER_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(registerRequest))

                    // Then
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").value("example@email.com"))
                    .andExpect(jsonPath("$.username").value("username"))
                    .andExpect(jsonPath("$.firstName").value("firstName"))
                    .andExpect(jsonPath("$.lastName").value("lastName"))
                    .andExpect(jsonPath("$.enabled").value(false))
                    .andExpect(jsonPath("$.roles").isArray())
                    .andExpect(jsonPath("$.roles", hasSize(2)))
                    .andExpect(jsonPath("$.roles", containsInAnyOrder("ROLE_USER", "ROLE_ADMIN")));
        }

        @Test
        void shouldReturnResponseWithEmailIsAlreadyInUse() throws Exception {
            // Given
            String registerRequest = """
                            {
                                "email": "example@email.com",
                                "password": "Password1!",
                                "confirmPassword": "Password1!",
                                "username": "username",
                                "firstName": "firstName",
                                "lastName": "lastName",
                                "birthDate": "2000-01-01T01:01:01",
                                "role": "attendee"
                            }
                    """;
            String ERROR_MESSAGE = "example@email.com is already in use";

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
                                "username": "username__",
                                "firstName": "firstName",
                                "lastName": "lastName",
                                "birthDate": "2000-01-01T01:01:01",
                                "role": "attendee"
                            }
                    """;
            String ERROR_MESSAGE = "username__ is already in use";

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
                                "username": "username__",
                                "firstName": "firstName",
                                "lastName": "lastName",
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
         * 2.User not found
         * 3.Incorrect verification code
         * 4.User already verified
         * 5.Blank values (ex. "email": "")
         * 6.Plain object (ex. {}) */

        @Test
        void shouldSuccessfullyVerifyUser() throws Exception {
            // Given
            String requestBody = """
                        {
                            "email": "example@email.com",
                            "verificationCode": "123123"
                        }
                    """;
            VerifyResponse verifyResponse = new VerifyResponse("example@email.com", true);

            // When
            when(authService.verify(any(VerifyRequest.class))).thenReturn(verifyResponse);
            mockMvc.perform(post(VERIFY_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(requestBody))

                    // Then
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").value("example@email.com"))
                    .andExpect(jsonPath("$.enabled").value(true));
        }

        @Test
        void shouldReturnResponseWithEmailNotFoundError() throws Exception {
            // Given
            String requestBody = """
                        {
                            "email": "example@email.com",
                            "verificationCode": "123123"
                        }
                    """;
            String ERROR_MESSAGE = "example@email.com is not found";

            // When
            when(authService.verify(any(VerifyRequest.class))).thenThrow(new UserNotFoundException(ERROR_MESSAGE));
            mockMvc.perform(post(VERIFY_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(requestBody))

                    // Then
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value(ERROR_MESSAGE));
        }

        @Test
        void shouldReturnResponseWithIncorrectVerificationCodeError() throws Exception {
            // Given
            String requestBody = """
                    {
                        "email": "example@email.com",
                        "verificationCode": "123123"
                    }
                    """;
            String ERROR_MESSAGE = "Incorrect verification code: 123123";

            // When
            when(authService.verify(any(VerifyRequest.class))).thenThrow(new IncorrectVerificationCodeException(ERROR_MESSAGE));
            mockMvc.perform(post(VERIFY_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(requestBody))

                    // Then
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(ERROR_MESSAGE));
        }

        @Test
        void shouldReturnResponseWithUserIsAlreadyVerifiedError() throws Exception {
            // Given
            String requestBody = """
                    {
                        "email": "example@email.com",
                        "verificationCode": "123123"
                    }
                    """;
            String ERROR_MESSAGE = "example@email.com is already verified";

            // When
            when(authService.verify(any(VerifyRequest.class))).thenThrow(new UserVerifiedException(ERROR_MESSAGE));
            mockMvc.perform(post(VERIFY_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(requestBody))

                    // Then
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").value(ERROR_MESSAGE));
        }

        @Test
        void shouldReturnResponseIncludingAllFieldErrors() throws Exception {
            // Given
            String requestBody = """
                            {
                                "email": "",
                                "verificationCode": ""
                            }
                    """;

            // When
            mockMvc.perform(post(VERIFY_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(requestBody))

                    // Then
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.email").isArray())
                    .andExpect(jsonPath("$.email", hasSize(1)))
                    .andExpect(jsonPath("$.verificationCode").isArray())
                    .andExpect(jsonPath("$.verificationCode", hasSize(1)));
        }

        @Test
        void shouldReturnResponseWithOnlyRequiredFieldsErrors() throws Exception {
            // When
            mockMvc.perform(post(VERIFY_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content("{}"))

                    // Then
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.email").isArray())
                    .andExpect(jsonPath("$.email", hasSize(1)))
                    .andExpect(jsonPath("$.verificationCode").isArray())
                    .andExpect(jsonPath("$.verificationCode", hasSize(1)));
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
            // Given
            String code = "123123";
            String RESPONSE_TEXT = "Verification code resent successfully!";
            String to = "?email=example@email.com";

            // When
            when(authService.resend(any(String.class))).thenReturn(code);
            mockMvc.perform(post(RESEND_ENDPOINT + to))

                    // Then
                    .andExpect(status().isOk())
                    .andExpect(content().string(RESPONSE_TEXT));
        }

        @Test
        void shouldReturnResponseWithUserNotFoundError() throws Exception {
            // Given
            String ERROR_MESSAGE = "example@email.com is not found";
            String to = "?email=example@email.com";

            // When
            when(authService.resend(any(String.class))).thenThrow(new UserNotFoundException(ERROR_MESSAGE));
            mockMvc.perform(post(RESEND_ENDPOINT + to))

                    // Then
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value(ERROR_MESSAGE));
        }

        @Test
        void shouldReturnResponseWithUserAlreadyVerifiedError() throws Exception {
            // Given
            String ERROR_MESSAGE = "example@email.com is already verified";
            String to = "?email=example@email.com";

            // When
            when(authService.resend(any(String.class))).thenThrow(new UserVerifiedException(ERROR_MESSAGE));
            mockMvc.perform(post(RESEND_ENDPOINT + to))

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

}
