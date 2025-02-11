package com.blankfactor.auth.controller;

import com.blankfactor.auth.repository.UserRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class AuthControllerIT {

    private static final String REGISTER_ENDPOINT = "/api/v1/auth/register";
    private static final String VERIFY_ENDPOINT = "/api/v1/auth/verify";
    private static final String RESEND_ENDPOINT = "/api/v1/auth/resend";

    private final MockMvc mockMvc;
    private final UserRepository userRepository;

    @Autowired
    public AuthControllerIT(MockMvc mockMvc, UserRepository userRepository) {
        this.mockMvc = mockMvc;
        this.userRepository = userRepository;
    }

    @BeforeEach
    public void setUp() {
        this.userRepository.deleteAll();
    }

    @Nested
    class RegisterTests {
        @Test
        void shouldSuccessfullySendRequestAndReceiveResponse() throws Exception {
            // Given
            String requestBody = """
                            {
                                "email": "example@email.com",
                                "password": "Password1!",
                                "confirmPassword": "Password1!",
                                "username": "john_doe",
                                "firstName": "John",
                                "lastName": "Doe",
                                "birthDate": "2000-01-01T01:01:01"
                            }
                    """;

            // When
            mockMvc.perform(post(REGISTER_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                    // Then
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").isNumber())
                    .andExpect(jsonPath("$.email").value("example@email.com"))
                    .andExpect(jsonPath("$.password").value(Matchers.matchesPattern("^\\$2[ayb]\\$\\d{2}\\$[./A-Za-z0-9]{53}$")))
                    .andExpect(jsonPath("$.username").value("john_doe"))
                    .andExpect(jsonPath("$.firstName").value("John"))
                    .andExpect(jsonPath("$.lastName").value("Doe"))
                    .andExpect(jsonPath("$.birthDate").value("2000-01-01T01:01:01"))
                    .andExpect(jsonPath("$.enabled").value(false))
                    .andExpect(jsonPath("$.verificationCode", hasLength(6)))
                    .andExpect(jsonPath("$.verificationCodeExpiresAt").value(
                            Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}")))
                    .andExpect(jsonPath("$.verificationCodeExpiresAt").value(
                            Matchers.allOf(
                                    Matchers.greaterThanOrEqualTo(LocalDateTime.now().plusMinutes(14).toString()),
                                    Matchers.lessThanOrEqualTo(LocalDateTime.now().plusMinutes(16).toString()))));
        }

        @Test
        void shouldReturnResponseWithEmailIsAlreadyInUse() throws Exception {
            // Given
            String requestBody = """
                            {
                                "email": "example@email.com",
                                "password": "Password1!",
                                "confirmPassword": "Password1!",
                                "username": "john_doe",
                                "firstName": "John",
                                "lastName": "Doe",
                                "birthDate": "2000-01-01T01:01:01"
                            }
                    """;

            // When
            mockMvc.perform(post(REGISTER_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(requestBody));
            mockMvc.perform(post(REGISTER_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(requestBody))

                    // Then
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.code").value("409"))
                    .andExpect(jsonPath("$.title").value("CONFLICT"))
                    .andExpect(jsonPath("$.message").value("example@email.com is already in use"));
        }

        @Test
        void shouldReturnResponseWithUsernameIsAlreadyInUse() throws Exception {
            // Given
            String requestBody_1 = """
                            {
                                "email": "example@email.com",
                                "password": "Password1!",
                                "confirmPassword": "Password1!",
                                "username": "john_doe",
                                "firstName": "John",
                                "lastName": "Doe",
                                "birthDate": "2000-01-01T01:01:01"
                            }
                    """;
            String requestBody_2 = """
                            {
                                "email": "new@email.com", 
                                "password": "Password1!",
                                "confirmPassword": "Password1!",
                                "username": "john_doe",
                                "firstName": "John",
                                "lastName": "Doe",
                                "birthDate": "2000-01-01T01:01:01"
                            }
                    """;

            // When
            mockMvc.perform(post(REGISTER_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(requestBody_1));
            mockMvc.perform(post(REGISTER_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(requestBody_2))

                    // Then
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.code").value("409"))
                    .andExpect(jsonPath("$.title").value("CONFLICT"))
                    .andExpect(jsonPath("$.message").value("john_doe is already in use"));
        }

        @Test
        void shouldReturnResponseWithPasswordsDoNotMatchError() throws Exception {
            // Given
            String requestBody = """
                            {
                                "email": "example@email.com",
                                "password": "Password1!",
                                "confirmPassword": "Password2!",
                                "username": "john_doe",
                                "firstName": "John",
                                "lastName": "Doe",
                                "birthDate": "2000-01-01T01:01:01"
                            }
                    """;

            // When
            mockMvc.perform(post(REGISTER_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(requestBody))

                    // Then
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("400"))
                    .andExpect(jsonPath("$.title").value("BAD_REQUEST"))
                    .andExpect(jsonPath("$.message").value("Passwords do not match. Please try again."));
        }
    }

    @Nested
    class VerifyTests {
        @Test
        void shouldSuccessfullyVerifyUser() throws Exception {
            // Given
            String registerRequestBody = """
                            {
                                "email": "example@email.com",
                                "password": "Password1!",
                                "confirmPassword": "Password1!",
                                "username": "john_doe",
                                "firstName": "John",
                                "lastName": "Doe",
                                "birthDate": "2000-01-01T01:01:01"
                            }
                    """;
            String verifyRequestBody = """
                        {
                            "email": "example@email.com",
                            "verificationCode": "%s"
                        }
                    """;

            // When
            mockMvc.perform(post(REGISTER_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(registerRequestBody));
            String code = userRepository.findByEmail("example@email.com").get().getVerificationCode();
            mockMvc.perform(post(VERIFY_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(String.format(verifyRequestBody, code)))

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

            // When
            mockMvc.perform(post(VERIFY_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(requestBody))

                    // Then
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value("404"))
                    .andExpect(jsonPath("$.title").value("NOT_FOUND"))
                    .andExpect(jsonPath("$.message").value("example@email.com is not found"));
        }

        @Test
        void shouldReturnResponseWithIncorrectVerificationCodeError() throws Exception {
            // Given
            String registerRequestBody = """
                            {
                                "email": "example@email.com",
                                "password": "Password1!",
                                "confirmPassword": "Password1!",
                                "username": "john_doe",
                                "firstName": "John",
                                "lastName": "Doe",
                                "birthDate": "2000-01-01T01:01:01"
                            }
                    """;
            String verifyRequestBody = """
                    {
                        "email": "example@email.com",
                        "verificationCode": "000000"
                    }
                    """;

            // When
            mockMvc.perform(post(REGISTER_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(registerRequestBody));
            mockMvc.perform(post(VERIFY_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(verifyRequestBody))

                    // Then
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("400"))
                    .andExpect(jsonPath("$.title").value("BAD_REQUEST"))
                    .andExpect(jsonPath("$.message").value(String.format("Incorrect verification code: 000000")));
        }

        @Test
        void shouldReturnResponseWithUserIsAlreadyVerifiedError() throws Exception {
            // Given
            String registerRequestBody = """
                            {
                                "email": "example@email.com",
                                "password": "Password1!",
                                "confirmPassword": "Password1!",
                                "username": "john_doe",
                                "firstName": "John",
                                "lastName": "Doe",
                                "birthDate": "2000-01-01T01:01:01"
                            }
                    """;
            String verifyRequestBody = """
                    {
                        "email": "example@email.com",
                        "verificationCode": "%s"
                    }
                    """;

            // When
            mockMvc.perform(post(REGISTER_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(registerRequestBody));
            String code = userRepository.findByEmail("example@email.com").get().getVerificationCode();
            mockMvc.perform(post(VERIFY_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(String.format(verifyRequestBody, code)));
            mockMvc.perform(post(VERIFY_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(String.format(verifyRequestBody, code)))

                    // Then
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.code").value("409"))
                    .andExpect(jsonPath("$.title").value("CONFLICT"))
                    .andExpect(jsonPath("$.message").value("example@email.com is already verified"));
        }
    }

    @Nested
    class ResendTests {
        @Test
        void shouldSuccessfullyResendVerificationEmail() throws Exception {
            // Given
            String registerRequestBody = """
                            {
                                "email": "example@email.com",
                                "password": "Password1!",
                                "confirmPassword": "Password1!",
                                "username": "john_doe",
                                "firstName": "John",
                                "lastName": "Doe",
                                "birthDate": "2000-01-01T01:01:01"
                            }
                    """;

            // When
            mockMvc.perform(post(REGISTER_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(registerRequestBody));
            String codeBefore = userRepository.findByEmail("example@email.com").get().getVerificationCode();
            mockMvc.perform(post(RESEND_ENDPOINT + "?email=example@email.com"))

                    // Then
                    .andExpect(status().isOk());
            String codeAfter = userRepository.findByEmail("example@email.com").get().getVerificationCode();
            assertNotEquals(codeBefore, codeAfter);
        }
    }
}

