package com.blankfactor.auth.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.blankfactor.auth.entity.Role;
import com.blankfactor.auth.entity.User;
import com.blankfactor.auth.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldSuccessfullyReturnTheExistingUsers() {
        // Given
        User user1 = new User(1L, "example1@email.com", "Pass_123", "UserName", "User", "Name", LocalDateTime.now(), false, null, null, Set.of(Role.ROLE_USER));
        User user2 = new User(2L, "example2@email.com", "456_Pass", "NameUser", "Name", "User", LocalDateTime.now(), true, null, null, Set.of(Role.ROLE_ADMIN));
        List<User> mockUsers = Arrays.asList(user1, user2);

        // When
        when(this.userRepository.findAll()).thenReturn(mockUsers);
        List<User> result = this.userService.getAllUsers();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("example1@email.com", result.get(0).getEmail());
        assertEquals("example2@email.com", result.get(1).getEmail());
        assertEquals("UserName", result.get(0).getUsername());
        assertEquals("NameUser", result.get(1).getUsername());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void shouldReturnEmptyListWhenThereArentUsers() {
        // When
        when(userRepository.findAll()).thenReturn(Collections.emptyList());
        List<User> result = userService.getAllUsers();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void shouldThrowAnExceptionWhenThereIsProblemWithTheRepository() {
        // When
        when(userRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        // Then
        Exception exception = assertThrows(RuntimeException.class, () -> userService.getAllUsers());
        assertEquals("Database error", exception.getMessage());
        verify(userRepository, times(1)).findAll();
    }

}
