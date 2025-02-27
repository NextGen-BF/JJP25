package com.backend.service.user;

import com.backend.entity.dto.outgoing.RegisterResponse;
import com.backend.entity.dto.incoming.RegisterRequest;
import com.backend.entity.user.User;
import com.backend.entity.user.UserType;
import com.backend.exception.user.UserFoundException;
import com.backend.repository.user.UserRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    private static final String TEST_ATTENDEE_ROLE = "ATTENDEE";
    private static final String TEST_ORGANISER_ROLE = "ORGANISER";

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Nested
    class RegisterTests {

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
            when(userRepository.findById(registerRequest.getId())).thenReturn(Optional.empty());
            RegisterResponse registerResponse = userService.register(registerRequest);

            // Then
            verify(userRepository).saveAndFlush(any(User.class));
            assertEquals(registerResponse.getId(), registerRequest.getId());
            assertEquals(registerResponse.getUserType(), registerRequest.getRole());
            assertEquals(TEST_ATTENDEE_ROLE, registerResponse.getUserType());
        }

        @Test
        public void shouldSuccessfullyRegisterAdmin() {
            // Given
            RegisterRequest registerRequest = RegisterRequest.builder()
                    .id(1L)
                    .role(TEST_ORGANISER_ROLE)
                    .build();

            // When
            when(userRepository.findById(registerRequest.getId())).thenReturn(Optional.empty());
            RegisterResponse registerResponse = userService.register(registerRequest);

            // Then
            verify(userRepository).saveAndFlush(any(User.class));
            assertEquals(registerResponse.getId(), registerRequest.getId());
            assertEquals(registerResponse.getUserType(), registerRequest.getRole());
            assertEquals(TEST_ORGANISER_ROLE, registerResponse.getUserType());
        }

        @Test
        public void shouldThrowUserFoundExceptionWhenUserWithIdIsFound() {
            // Given
            RegisterRequest registerRequest = RegisterRequest.builder()
                    .id(1L)
                    .role(TEST_ATTENDEE_ROLE)
                    .build();
            User user = User.builder()
                    .id(1L)
                    .type(UserType.ATTENDEE)
                    .build();

            // When
            when(userRepository.findById(registerRequest.getId())).thenReturn(Optional.of(user));

            // Then
            UserFoundException userFoundException = assertThrows(UserFoundException.class, () -> userService.register(registerRequest));
            assertEquals("User with id: 1 already exists.", userFoundException.getMessage());
        }

    }


}
