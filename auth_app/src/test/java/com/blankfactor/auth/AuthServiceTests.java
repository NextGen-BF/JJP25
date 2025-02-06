package com.blankfactor.auth;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import com.blankfactor.auth.exception.custom.invalid.InvalidPasswordException;
import com.blankfactor.auth.exception.custom.user.UserNotFoundException;
import com.blankfactor.auth.exception.custom.user.UserNotVerifiedException;
import com.blankfactor.auth.model.User;
import com.blankfactor.auth.model.dto.LoginUserDto;
import com.blankfactor.auth.repository.UserRepository;
import com.blankfactor.auth.service.AuthService;
import com.blankfactor.auth.service.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.thymeleaf.TemplateEngine;

@ExtendWith(MockitoExtension.class)
class AuthServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @Mock
    private TemplateEngine templateEngine;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    /**
     * Test that a valid login returns the user.
     */
    @Test
    void login_withValidCredentials_returnsUser() {
        LoginUserDto loginUserDto = new LoginUserDto();
        loginUserDto.setEmail("test@example.com");
        loginUserDto.setPassword("password");

        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("encodedPassword")
                .enabled(true)
                .build();

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);

        User result = authService.login(loginUserDto);

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getEmail(), result.getEmail());
    }

    /**
     * Test that login throws a UserNotFoundException when the user does not exist.
     */
    @Test
    void login_withNonExistentUser_throwsUserNotFoundException() {
        LoginUserDto loginUserDto = new LoginUserDto();
        loginUserDto.setEmail("nonexistent@example.com");
        loginUserDto.setPassword("password");

        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> authService.login(loginUserDto));
    }

    /**
     * Test that login fails when the user exists but is not verified.
     */
    @Test
    void login_withUnverifiedUser_throwsUserNotVerifiedException() {
        LoginUserDto loginUserDto = new LoginUserDto();
        loginUserDto.setEmail("test@example.com");
        loginUserDto.setPassword("password");

        User user = User.builder()
                .id(2L)
                .email("test@example.com")
                .password("encodedPassword")
                .enabled(false)
                .build();

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        Exception exception = assertThrows(UserNotVerifiedException.class, () -> authService.login(loginUserDto));
        assertTrue(exception.getMessage().contains("Account is not verified"));
    }

    /**
     * Test that login fails when the authentication manager rejects the credentials.
     */
    @Test
    void login_withInvalidCredentials_throwsBadCredentialsException() {
        LoginUserDto loginUserDto = new LoginUserDto();
        loginUserDto.setEmail("test@example.com");
        loginUserDto.setPassword("wrongPassword");

        User user = User.builder()
                .id(3L)
                .email("test@example.com")
                .password("encodedPassword")
                .enabled(true)
                .build();

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(InvalidPasswordException.class, () -> authService.login(loginUserDto));
    }
}
