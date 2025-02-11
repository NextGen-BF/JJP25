package com.blankfactor.auth.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import com.blankfactor.auth.exception.custom.user.UserNotVerifiedException;
import com.blankfactor.auth.exception.custom.InvalidPasswordException;
import com.blankfactor.auth.entity.dto.imp.LoginRequest;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Nested
    class LoginTests {
        @Test
        void loginWithValidEmailReturnsUser() {
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setLoginIdentifier(TEST_EMAIL);
            loginRequest.setPassword("password");

            User user = User.builder()
                    .id(1L)
                    .email(TEST_EMAIL)
                    .username(TEST_USERNAME)
                    .password("encodedPassword")
                    .enabled(true)
                    .build();

            when(userRepository.findByEmailOrUsername(TEST_EMAIL))
                    .thenReturn(Optional.of(user));

            User result = authService.login(loginRequest);

            assertNotNull(result);
            assertEquals(user.getId(), result.getId());
            assertEquals(user.getEmail(), result.getEmail());
        }

        @Test
        void loginWithValidUsernameReturnsUser() {
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setLoginIdentifier(TEST_USERNAME);
            loginRequest.setPassword("password");

            User user = User.builder()
                    .id(1L)
                    .email(TEST_EMAIL)
                    .username(TEST_USERNAME)
                    .password("encodedPassword")
                    .enabled(true)
                    .build();

            when(userRepository.findByEmailOrUsername(TEST_USERNAME))
                    .thenReturn(Optional.of(user));

            User result = authService.login(loginRequest);

            assertNotNull(result);
            assertEquals(user.getId(), result.getId());
            assertEquals(user.getEmail(), result.getEmail());
        }

        @Test
        void loginWithNonExistentUserThrowsUserNotFoundException() {
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setLoginIdentifier("nonexistent@example.com");
            loginRequest.setPassword("password");

            when(userRepository.findByEmailOrUsername("nonexistent@example.com"))
                    .thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class, () -> authService.login(loginRequest));
        }

        @Test
        void loginWithUnverifiedUserThrowsUserNotVerifiedException() {
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setLoginIdentifier(TEST_EMAIL);
            loginRequest.setPassword("password");

            User user = User.builder()
                    .id(2L)
                    .email(TEST_EMAIL)
                    .username(TEST_USERNAME)
                    .password("encodedPassword")
                    .enabled(false)
                    .build();

            when(userRepository.findByEmailOrUsername(TEST_EMAIL))
                    .thenReturn(Optional.of(user));

            Exception exception = assertThrows(UserNotVerifiedException.class, () -> authService.login(loginRequest));
            assertTrue(exception.getMessage().contains("Account is not verified"));
        }

        @Test
        void loginWithInvalidCredentialsThrowsInvalidPasswordException() {
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setLoginIdentifier(TEST_EMAIL);
            loginRequest.setPassword("wrongPassword");

            User user = User.builder()
                    .id(3L)
                    .email(TEST_EMAIL)
                    .username(TEST_USERNAME)
                    .password("encodedPassword")
                    .enabled(true)
                    .build();

            when(userRepository.findByEmailOrUsername(TEST_EMAIL))
                    .thenReturn(Optional.of(user));

            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenThrow(new BadCredentialsException("Bad credentials"));

            assertThrows(InvalidPasswordException.class, () -> authService.login(loginRequest));
        }

        @Test
        void loginWithNullIdentifierThrowsIllegalArgumentException() {
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setLoginIdentifier(null);
            loginRequest.setPassword("password");

            assertThrows(IllegalArgumentException.class, () -> authService.login(loginRequest));
        }

        @Test
        void loginWithEmptyIdentifierThrowsIllegalArgumentException() {
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setLoginIdentifier("");
            loginRequest.setPassword("password");

            assertThrows(IllegalArgumentException.class, () -> authService.login(loginRequest));
        }

        @Test
        void loginWithBlankIdentifierThrowsIllegalArgumentException() {
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setLoginIdentifier("   ");
            loginRequest.setPassword("password");

            assertThrows(IllegalArgumentException.class, () -> authService.login(loginRequest));
        }
    }
}