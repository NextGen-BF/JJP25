package com.blankfactor.auth.controller;

import com.blankfactor.auth.entity.dto.exp.RegisterResponse;
import com.blankfactor.auth.entity.dto.exp.VerifyResponse;
import com.blankfactor.auth.entity.dto.imp.RegisterRequest;
import com.blankfactor.auth.entity.dto.imp.VerifyRequest;
import com.blankfactor.auth.exception.custom.PasswordsDoNotMatchException;
import com.blankfactor.auth.exception.custom.code.IncorrectVerificationCodeException;
import com.blankfactor.auth.exception.custom.user.UserFoundException;
import com.blankfactor.auth.exception.custom.user.UserNotFoundException;
import com.blankfactor.auth.exception.custom.user.UserVerifiedException;
import com.blankfactor.auth.service.AuthService;
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

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
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
public class AuthControllerTest {

    private static final String REGISTER_ENDPOINT = "/api/v1/auth/register";
    private static final String VERIFY_ENDPOINT = "/api/v1/auth/verify";
    private static final String RESEND_ENDPOINT = "/api/v1/auth/resend";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Nested
    class RegisterTests {

        /* ---> TEST CASES <---
        * 1.Successful registration
        * 2.Email already in use
        * 3.Username already in use
        * 4.Passwords do not match
        * 5.Blank values (ex. "email": "")
        * 6.Plain object (ex. {})
        * */

        @Test
        void shouldSuccessfullySendRequestAndReceiveResponse() throws Exception {
            // Given
            RegisterResponse registerResponse = new RegisterResponse(
                    1L, "example@email.com", "hashed-password",
                    "username", "firstName", "lastName", LocalDateTime.parse("2000-01-01T01:01:01"),
                    false, "123123", LocalDateTime.parse("2000-01-01T01:15:01"));
            String requestBody = """
                            {
                                "email": "example@email.com",
                                "password": "Password1!",
                                "confirmPassword": "Password1!",
                                "username": "username",
                                "firstName": "firstName",
                                "lastName": "lastName",
                                "birthDate": "2000-01-01T01:01:01"
                            }
                    """;

            // When
            doReturn(registerResponse).when(authService).register(any(RegisterRequest.class));
            mockMvc.perform(post(REGISTER_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(requestBody))

                    // Then
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.email").value("example@email.com"))
                    .andExpect(jsonPath("$.password").value("hashed-password"))
                    .andExpect(jsonPath("$.username").value("username"))
                    .andExpect(jsonPath("$.firstName").value("firstName"))
                    .andExpect(jsonPath("$.lastName").value("lastName"))
                    .andExpect(jsonPath("$.birthDate").value("2000-01-01T01:01:01"))
                    .andExpect(jsonPath("$.enabled").value(false))
                    .andExpect(jsonPath("$.verificationCode").value("123123"))
                    .andExpect(jsonPath("$.verificationCodeExpiresAt").value("2000-01-01T01:15:01"));
        }

        @Test
        void shouldReturnResponseWithEmailIsAlreadyInUse() throws Exception {
            // Given
            String requestBody = """
                            {
                                "email": "example@email.com",
                                "password": "Password1!",
                                "confirmPassword": "Password1!",
                                "username": "username",
                                "firstName": "firstName",
                                "lastName": "lastName",
                                "birthDate": "2000-01-01T01:01:01"
                            }
                    """;
            String ERROR_MESSAGE = "example@email.com is already in use";

            // When
            when(authService.register(any(RegisterRequest.class))).thenThrow(new UserFoundException(ERROR_MESSAGE));
            mockMvc.perform(post(REGISTER_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(requestBody))

                    // Then
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").value(ERROR_MESSAGE));
        }

        @Test
        void shouldReturnResponseWithUsernameIsAlreadyInUse() throws Exception {
            // Given
            String requestBody = """
                            {
                                "email": "example@email.com",
                                "password": "Password1!",
                                "confirmPassword": "Password1!",
                                "username": "username__",
                                "firstName": "firstName",
                                "lastName": "lastName",
                                "birthDate": "2000-01-01T01:01:01"
                            }
                    """;
            String ERROR_MESSAGE = "username__ is already in use";

            // When
            when(authService.register(any(RegisterRequest.class))).thenThrow(new UserFoundException(ERROR_MESSAGE));
            mockMvc.perform(post(REGISTER_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(requestBody))

                    // Then
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").value(ERROR_MESSAGE));
        }

        @Test
        void shouldReturnResponseWithPasswordsDoNotMatchError() throws Exception {
            // Given
            String requestBody = """
                            {
                                "email": "example@email.com",
                                "password": "Password1!",
                                "confirmPassword": "Password2!",
                                "username": "username__",
                                "firstName": "firstName",
                                "lastName": "lastName",
                                "birthDate": "2000-01-01T01:01:01"
                            }
                    """;
            String ERROR_MESSAGE = "Passwords do not match. Please try again.";

            // When
            when(authService.register(any(RegisterRequest.class))).thenThrow(new PasswordsDoNotMatchException(ERROR_MESSAGE));
            mockMvc.perform(post(REGISTER_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(requestBody))

                    // Then
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(ERROR_MESSAGE));
        }


        @Test
        void shouldReturnResponseIncludingAllFieldErrors() throws Exception {
            // Given
            String requestBody = """
                            {
                              "email": "",
                              "password": "",
                              "confirmPassword": "",
                              "username": "",
                              "firstName": "",
                              "lastName": "",
                              "birthDate": ""
                            }
                    """;

            // When
            mockMvc.perform(post(REGISTER_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(requestBody))

                    // Then
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.email").isArray())
                    .andExpect(jsonPath("$.email", hasSize(2)))
                    .andExpect(jsonPath("$.password").isArray())
                    .andExpect(jsonPath("$.password", hasSize(2)))
                    .andExpect(jsonPath("$.confirmPassword").isArray())
                    .andExpect(jsonPath("$.confirmPassword", hasSize(2)))
                    .andExpect(jsonPath("$.username").isArray())
                    .andExpect(jsonPath("$.username", hasSize(2)))
                    .andExpect(jsonPath("$.firstName").isArray())
                    .andExpect(jsonPath("$.firstName", hasSize(2)))
                    .andExpect(jsonPath("$.lastName").isArray())
                    .andExpect(jsonPath("$.lastName", hasSize(2)))
                    .andExpect(jsonPath("$.birthDate").isArray())
                    .andExpect(jsonPath("$.birthDate", hasSize(1)));
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
                    .andExpect(jsonPath("$.birthDate", hasSize(1)));
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
            VerifyResponse verifyResponse = new VerifyResponse(
                    "example@email.com",
                    true,
                    null,
                    null);

            // When
            when(authService.verify(any(VerifyRequest.class))).thenReturn(verifyResponse);
            mockMvc.perform(post(VERIFY_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(requestBody))

                    // Then
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").value("example@email.com"))
                    .andExpect(jsonPath("$.enabled").value(true))
                    .andExpect(jsonPath("$.verificationCode").value(nullValue()))
                    .andExpect(jsonPath("$.verificationCodeExpiresAt").value(nullValue()));
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
                    .andExpect(jsonPath("$.email", hasSize(2)))
                    .andExpect(jsonPath("$.verificationCode").isArray())
                    .andExpect(jsonPath("$.verificationCode", hasSize(2)));
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
            String RESPONSE_TEXT = "Verification code resent successfully! New code: ";
            String to = "?email=example@email.com";

            // When
            when(authService.resendVerificationCode(any(String.class))).thenReturn(code);
            mockMvc.perform(post(RESEND_ENDPOINT + to))

                    // Then
                    .andExpect(status().isOk())
                    .andExpect(content().string(RESPONSE_TEXT + code));
        }

        @Test
        void shouldReturnResponseWithUserNotFoundError() throws Exception {
            // Given
            String ERROR_MESSAGE = "example@email.com is not found";
            String to = "?email=example@email.com";

            // When
            when(authService.resendVerificationCode(any(String.class))).thenThrow(new UserNotFoundException(ERROR_MESSAGE));
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
            when(authService.resendVerificationCode(any(String.class))).thenThrow(new UserVerifiedException(ERROR_MESSAGE));
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

}
