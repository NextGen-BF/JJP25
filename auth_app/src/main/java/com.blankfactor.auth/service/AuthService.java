package com.blankfactor.auth.service;

import com.blankfactor.auth.entity.dto.imp.LoginRequest;
import com.blankfactor.auth.exception.custom.InvalidCredentialsException;
import com.blankfactor.auth.exception.custom.user.UserNotVerifiedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class AuthService {
    private static final String USER_NOT_VERIFIED = "Account is not verified!";
    private static final String INVALID_CREDENTIALS = "Incorrect username/email or password.";

    private final AuthenticationManager authenticationManager;

    public User login(LoginRequest input) {
        if (input.getLoginIdentifier() == null || input.getLoginIdentifier().trim().isEmpty()) {
            log.warn("Login identifier is null or empty");
            throw new IllegalArgumentException("Login identifier cannot be null or empty");
        }

        log.debug("Attempting to log in user with identifier: {}", input.getLoginIdentifier());

        User user = userRepository.findByEmailOrUsername(input.getLoginIdentifier())
                .orElseThrow(() -> {
                    log.warn("User not found with identifier: {}", input.getLoginIdentifier());
                    return new InvalidCredentialsException(INVALID_CREDENTIALS);
                });

        if (!user.isEnabled()) {
            log.warn("User account is not verified: {}", input.getLoginIdentifier());
            throw new UserNotVerifiedException(USER_NOT_VERIFIED);
        }

        try {
            log.debug("Authenticating user credentials for: {}", input.getLoginIdentifier());
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getEmail(),
                            input.getPassword()
                    )
            );
            log.info("User authenticated successfully: {}", input.getLoginIdentifier());
        } catch (BadCredentialsException e) {
            log.warn("Invalid password for user: {}", input.getLoginIdentifier());
            throw new InvalidCredentialsException(INVALID_CREDENTIALS);
        }

        log.debug("Returning user details for: {}", input.getLoginIdentifier());
        return user;
    }
}