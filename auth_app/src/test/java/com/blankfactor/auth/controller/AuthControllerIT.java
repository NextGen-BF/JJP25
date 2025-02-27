package com.blankfactor.auth.controller;

import com.blankfactor.auth.entity.Role;
import com.blankfactor.auth.repository.UserRepository;
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

    private static final String TEST_EMAIL = "example@email.com";
    private static final String TEST_USERNAME = "john_doe";
    private static final String TEST_FIRST_NAME = "John";
    private static final String TEST_LAST_NAME = "Doe";

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
        void shouldSuccessfullySendRequestAndReceiveResponseForUser() throws Exception {
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

            // When
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
                    .andExpect(jsonPath("$.roles[0]").value(Role.ROLE_USER.name()))
                    .andExpect(jsonPath("$.roles[1]").value(Role.ROLE_ADMIN.name()));
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

            // When
            mockMvc.perform(post(REGISTER_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(registerRequest));
            mockMvc.perform(post(REGISTER_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(registerRequest))

                    // Then
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.code").value("409"))
                    .andExpect(jsonPath("$.title").value("CONFLICT"))
                    .andExpect(jsonPath("$.message").value(TEST_EMAIL + " is already in use"));
        }

        @Test
        void shouldReturnResponseWithUsernameIsAlreadyInUse() throws Exception {
            // Given
            String registerRequest1 = """
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
            String registerRequest2 = """
                            {
                                "email": "new@email.com", 
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
            mockMvc.perform(post(REGISTER_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(registerRequest1));
            mockMvc.perform(post(REGISTER_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(registerRequest2))

                    // Then
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.code").value("409"))
                    .andExpect(jsonPath("$.title").value("CONFLICT"))
                    .andExpect(jsonPath("$.message").value(TEST_USERNAME + " is already in use"));
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

            // When
            mockMvc.perform(post(REGISTER_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(registerRequest))

                    // Then
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("400"))
                    .andExpect(jsonPath("$.title").value("BAD_REQUEST"))
                    .andExpect(jsonPath("$.message").value("Passwords do not match. Please try again."));
        }
    }

    @Nested
    class VerifyTests {
        @Test // TODO: depends on ems_app, because makes an actual request
        void shouldSuccessfullyVerifyUser() throws Exception {
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
            String verifyRequest = """
                        {
                            "email": "example@email.com",
                            "verificationCode": "%s"
                        }
                    """;

            // When
            mockMvc.perform(post(REGISTER_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(registerRequest));
            String code = userRepository.findByEmail("example@email.com").get().getVerificationCode();
            mockMvc.perform(post(VERIFY_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(String.format(verifyRequest, code)))

                    // Then
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").value("example@email.com"))
                    .andExpect(jsonPath("$.enabled").value(true));
        }

        @Test
        void shouldReturnResponseWithEmailNotFoundError() throws Exception {
            // Given
            String verifyRequest = """
                        {
                            "email": "example@email.com",
                            "verificationCode": "123123"
                        }
                    """;

            // When
            mockMvc.perform(post(VERIFY_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(verifyRequest))

                    // Then
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value("404"))
                    .andExpect(jsonPath("$.title").value("NOT_FOUND"))
                    .andExpect(jsonPath("$.message").value(TEST_EMAIL + " is not found"));
        }

        @Test
        void shouldReturnResponseWithIncorrectVerificationCodeError() throws Exception {
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
            String verifyRequest = """
                    {
                        "email": "example@email.com",
                        "verificationCode": "000000"
                    }
                    """;

            // When
            mockMvc.perform(post(REGISTER_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(registerRequest));
            mockMvc.perform(post(VERIFY_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(verifyRequest))

                    // Then
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("400"))
                    .andExpect(jsonPath("$.title").value("BAD_REQUEST"))
                    .andExpect(jsonPath("$.message").value(String.format("Incorrect verification code: 000000")));
        }

        @Test // TODO: depends on ems_app, because makes an actual request
        void shouldReturnResponseWithUserIsAlreadyVerifiedError() throws Exception {
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
            String verifyRequest = """
                    {
                        "email": "example@email.com",
                        "verificationCode": "%s"
                    }
                    """;

            // When
            mockMvc.perform(post(REGISTER_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(registerRequest));
            String code = userRepository.findByEmail(TEST_EMAIL).get().getVerificationCode();
            mockMvc.perform(post(VERIFY_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(String.format(verifyRequest, code)));
            mockMvc.perform(post(VERIFY_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(String.format(verifyRequest, code)))

                    // Then
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.code").value("409"))
                    .andExpect(jsonPath("$.title").value("CONFLICT"))
                    .andExpect(jsonPath("$.message").value(TEST_EMAIL + " is already verified"));
        }
    }

    @Nested
    class ResendTests {
        @Test
        void shouldSuccessfullyResendVerificationEmail() throws Exception {
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

            // When
            mockMvc.perform(post(REGISTER_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(registerRequest));
            String codeBefore = userRepository.findByEmail(TEST_EMAIL).get().getVerificationCode();
            mockMvc.perform(post(RESEND_ENDPOINT + "?email=" + TEST_EMAIL))

                    // Then
                    .andExpect(status().isOk());
            String codeAfter = userRepository.findByEmail(TEST_EMAIL).get().getVerificationCode();
            assertNotEquals(codeBefore, codeAfter);
        }
    }
}

