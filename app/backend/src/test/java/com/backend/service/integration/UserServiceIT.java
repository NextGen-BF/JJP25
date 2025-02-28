package com.backend.service.integration;

import com.backend.entity.dto.incoming.RegisterRequest;
import com.backend.entity.dto.outgoing.RegisterResponse;
import com.backend.exception.user.UserFoundException;
import com.backend.repository.user.UserRepository;
import com.backend.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class UserServiceIT {

    private static final String TEST_ATTENDEE_ROLE = "ATTENDEE";
    private static final String TEST_ORGANISER_ROLE = "ORGANISER";
    private static final String USER_FOUND = "User with id: %s already exists.";

    private final UserRepository userRepository;
    private final UserService userService;

    @Autowired
    public UserServiceIT(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @BeforeEach
    public void setup() {
        this.userRepository.deleteAll();
    }

    @Nested
    class RegisterTest {

        /* ---> TEST CASES <---
         * 1.Successful registration (User)
         * 2.Successful registration (Admin)
         * 3.Failed registration - UserFoundException */

        @Test
        public void shouldSuccessfullyRegisterUser() {
            // Given
            RegisterRequest registerRequest = RegisterRequest.builder()
                    .id(1L)
                    .role(TEST_ATTENDEE_ROLE)
                    .build();

            // When
            RegisterResponse registerResponse = userService.register(registerRequest);

            // Then
            assertEquals(registerResponse.getId(), registerRequest.getId());
            assertNull(registerResponse.getPhone());
            assertNull(registerResponse.getProfilePicture());
            assertEquals(registerResponse.getUserType(), registerRequest.getRole());
            assertNotNull(registerResponse.getCreatedAt());
            assertNotNull(registerResponse.getUpdatedAt());
        }

        @Test
        public void shouldSuccessfullyRegisterAdmin() {
            // Given
            RegisterRequest registerRequest = RegisterRequest.builder()
                    .id(1L)
                    .role(TEST_ORGANISER_ROLE)
                    .build();

            // When
            RegisterResponse registerResponse = userService.register(registerRequest);

            // Then
            assertEquals(registerRequest.getId(), registerResponse.getId());
            assertNull(registerResponse.getPhone());
            assertNull(registerResponse.getProfilePicture());
            assertEquals(registerRequest.getRole(), registerResponse.getUserType());
            assertNotNull(registerResponse.getCreatedAt());
            assertNotNull(registerResponse.getUpdatedAt());
        }

        @Test
        public void shouldFailRegistrationWhenTheUserIsFound() {
            // Given
            RegisterRequest registerRequest = RegisterRequest.builder()
                    .id(1L)
                    .role(TEST_ATTENDEE_ROLE)
                    .build();

            // When
            userService.register(registerRequest);

            // Then
            UserFoundException userFoundException = assertThrows(UserFoundException.class, () -> userService.register(registerRequest));
            assertEquals(String.format(USER_FOUND, 1L), userFoundException.getMessage());
        }

    }

}
