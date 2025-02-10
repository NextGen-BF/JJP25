package com.blankfactor.auth.controller;

import com.blankfactor.auth.entity.dto.exp.RegisterResponse;
import com.blankfactor.auth.entity.dto.imp.RegisterRequest;
import com.blankfactor.auth.exception.custom.PasswordsDoNotMatchException;
import com.blankfactor.auth.exception.custom.user.UserFoundException;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith({MockitoExtension.class, SpringExtension.class})
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = {AuthController.class})
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Nested
    class RegisterTests {
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
                    }""";

            // When
            doReturn(registerResponse).when(authService).register(any(RegisterRequest.class));
            mockMvc.perform(post("/api/v1/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
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
                    }""";

            // When
            when(authService.register(any(RegisterRequest.class))).thenThrow(new UserFoundException("example@email.com is already in use"));
            mockMvc.perform(post("/api/v1/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    // Then
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").value("example@email.com is already in use"));
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
                    }""";

            // When
            when(authService.register(any(RegisterRequest.class))).thenThrow(new UserFoundException("username__ is already in use"));
            mockMvc.perform(post("/api/v1/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    // Then
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").value("username__ is already in use"));
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
                    }""";

            // When
            when(authService.register(any(RegisterRequest.class))).thenThrow(new PasswordsDoNotMatchException("Passwords do not match. Please try again."));
            mockMvc.perform(post("/api/v1/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    // Then
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Passwords do not match. Please try again."));
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
                    }""";

            // When
            mockMvc.perform(post("/api/v1/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
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
            mockMvc.perform(post("/api/v1/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
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


}