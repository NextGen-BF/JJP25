package com.backend.controller.user;

import com.backend.repository.user.UserRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.support.NullValue;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.regex.Matcher;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class UserControllerIT {

    // Expires at Aug 29 2034
    private static final String VALID_JWT_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6WyJST0xFX1VTRVIiXSwic3ViIjoiam9obl9kb2UzIiwiaWF0IjoxNzQwNDkwNDIxLCJleHAiOjIwNDA0OTQwMjF9.pLcvmCgtbB_VEWG-qLSLBFGio6NGaxLUDkMl3jrm4xw";

    private static final String REGISTER_ENDPOINT = "/api/v1/auth/register";

    private static final String TEST_ATTENDEE_ROLE = "ATTENDEE";
    private static final String TEST_ORGANISER_ROLE = "ORGANISER";

    private final MockMvc mockMvc;
    private final UserRepository userRepository;

    @Autowired
    public UserControllerIT(MockMvc mockMvc, UserRepository userRepository) {
        this.mockMvc = mockMvc;
        this.userRepository = userRepository;
    }

    @BeforeEach
    public void setUp() {
        this.userRepository.deleteAll();
    }

    @Nested
    class RegisterTests {

        /* ---> TEST CASES <---
         * 1.Successful registration (User)
         * 2.Successful registration (Admin)
         * 3.Failed registration - UserFoundException */

        @Test
        public void shouldSuccessfullyRegisterUser() throws Exception {
            // Given
            String registerRequest = """
                    {
                        "id": 1,
                        "role": "ATTENDEE"
                    }
                    """;

            // When
            mockMvc.perform(post(REGISTER_ENDPOINT).contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + VALID_JWT_TOKEN)
                            .content(registerRequest))

                    // Then
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.phone").value(is(nullValue())))
                    .andExpect(jsonPath("$.profilePicture").value(is(nullValue())))
                    .andExpect(jsonPath("$.userType").value(TEST_ATTENDEE_ROLE))
                    .andExpect(jsonPath("$.createdAt").value(is(notNullValue())))
                    .andExpect(jsonPath("$.updatedAt").value(is(notNullValue())));
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

            // When
            mockMvc.perform(post(REGISTER_ENDPOINT).contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + VALID_JWT_TOKEN)
                            .content(registerRequest))

                    // Then
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.phone").value(is(nullValue())))
                    .andExpect(jsonPath("$.profilePicture").value(is(nullValue())))
                    .andExpect(jsonPath("$.userType").value(TEST_ORGANISER_ROLE))
                    .andExpect(jsonPath("$.createdAt").value(is(notNullValue())))
                    .andExpect(jsonPath("$.updatedAt").value(is(notNullValue())));
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

            // When
            mockMvc.perform(post(REGISTER_ENDPOINT).contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + VALID_JWT_TOKEN)
                    .content(registerRequest));
            mockMvc.perform(post(REGISTER_ENDPOINT).contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + VALID_JWT_TOKEN)
                            .content(registerRequest))

                    // Then
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.code").value("409"))
                    .andExpect(jsonPath("$.title").value("CONFLICT"))
                    .andExpect(jsonPath("$.message").value("User with id: 1 already exists."));
        }

    }


}
