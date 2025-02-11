package com.blankfactor.auth.controller;

import com.blankfactor.auth.entity.User;
import com.blankfactor.auth.entity.dto.imp.LoginRequest;
import com.blankfactor.auth.exception.custom.InvalidPasswordException;
import com.blankfactor.auth.exception.custom.user.UserNotVerifiedException;
import com.blankfactor.auth.service.JwtService;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AuthControllerTest {
    private static final String LOGIN_ENDPOINT = "/api/v1/auth/login";

    @MockitoBean
    private JwtService jwtService;

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

            when(authService.login(any(LoginRequest.class))).thenThrow(new InvalidPasswordException(errorMessage));

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