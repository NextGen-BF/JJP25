package com.backend.controller.user;

import com.backend.entity.dto.exp.RegisterResponse;
import com.backend.entity.dto.imp.RegisterRequest;
import com.backend.exception.user.UserFoundException;
import com.backend.service.user.UserService;
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

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({MockitoExtension.class, SpringExtension.class})
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = {UserController.class})
public class UserControllerTest {

    private static final String REGISTER_ENDPOINT = "/api/v1/auth/register";

    private static final String TEST_ATTENDEE_ROLE = "ATTENDEE";
    private static final String TEST_ORGANISER_ROLE = "ORGANISER";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Nested
    class RegisterTests {

        /* ---> TEST CASES <---
         * 1.Successful registration (User)
         * 2.Successful registration (Admin)
         * 3.Failed registration - UserFoundException
         * 4.Invalid request - blank value(ex. "id": "")
         * 5.Invalid request - plain object (ex. {}) */

        @Test
        public void shouldSuccessfullyRegisterUser() throws Exception {
            // Given
            String registerRequest = """
                    {
                        "id": 1,
                        "role": "ATTENDEE"
                    }
                    """;
            RegisterResponse registerResponse = RegisterResponse.builder()
                    .id(1L)
                    .userType(TEST_ATTENDEE_ROLE)
                    .build();
            // When
            when(userService.register(any(RegisterRequest.class))).thenReturn(registerResponse);
            mockMvc.perform(post(REGISTER_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(registerRequest))

                    // Then
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(registerResponse.getId()))
                    .andExpect(jsonPath("$.userType").value(registerResponse.getUserType()));
        }

        @Test
        public void shouldSuccessfullyRegisterAdmin() throws Exception {
            // Given
            String registerRequest = """
                    {
                        "id": 1,
                        "role": "ORGANISER"
                    }
                    """;
            RegisterResponse registerResponse = RegisterResponse.builder()
                    .id(1L)
                    .userType(TEST_ORGANISER_ROLE)
                    .build();
            // When
            when(userService.register(any(RegisterRequest.class))).thenReturn(registerResponse);
            mockMvc.perform(post(REGISTER_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(registerRequest))

                    // Then
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(registerResponse.getId()))
                    .andExpect(jsonPath("$.userType").value(registerResponse.getUserType()));
        }

        @Test
        public void shouldThrowUserFoundExceptionWhenUserWithIdIsFound() throws Exception {
            // Given
            String registerRequest = """
                    {
                        "id": 1,
                        "role": "ATTENDEE"
                    }
                    """;
            String ERROR_MESSAGE = "User with id: 1 already exists.";

            // When
            when(userService.register(any(RegisterRequest.class))).thenThrow(new UserFoundException(ERROR_MESSAGE));
            mockMvc.perform(post(REGISTER_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(registerRequest))

                    // Then
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").value(ERROR_MESSAGE));
        }

        @Test
        public void shouldNotAcceptTheRequestDueToBlankValues() throws Exception {
            // Given
            String registerRequest = """
                    {
                        "id": "",
                        "role": ""
                    }
                    """;

            // When
            mockMvc.perform(post(REGISTER_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(registerRequest))

                    // Then
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.id").isArray())
                    .andExpect(jsonPath("$.id", hasSize(1)))
                    .andExpect(jsonPath("$.id[0]").value("User id is required."))
                    .andExpect(jsonPath("$.role").isArray())
                    .andExpect(jsonPath("$.role", hasSize(1)))
                    .andExpect(jsonPath("$.role[0]").value("User role is invalid."));
        }

        @Test
        public void shouldNotAcceptTheRequestDueToPlainObject() throws Exception {
            // Given
            String registerRequest = "{}";

            // When
            mockMvc.perform(post(REGISTER_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(registerRequest))

                    // Then
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.id").isArray())
                    .andExpect(jsonPath("$.id", hasSize(1)))
                    .andExpect(jsonPath("$.id[0]").value("User id is required."))
                    .andExpect(jsonPath("$.role").isArray())
                    .andExpect(jsonPath("$.role", hasSize(1)))
                    .andExpect(jsonPath("$.role[0]").value("User role is required."));
        }

    }

}
