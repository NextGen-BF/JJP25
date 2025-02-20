package com.blankfactor.auth.service;

import com.blankfactor.auth.entity.Role;
import com.blankfactor.auth.entity.dto.imp.LoginRequest;
import com.blankfactor.auth.exception.custom.*;
import com.blankfactor.auth.exception.custom.code.ExpiredVerificationCodeException;
import com.blankfactor.auth.exception.custom.code.IncorrectVerificationCodeException;
import com.blankfactor.auth.exception.custom.user.UserFoundException;
import com.blankfactor.auth.exception.custom.user.UserNotFoundException;
import com.blankfactor.auth.exception.custom.user.UserNotVerifiedException;
import com.blankfactor.auth.exception.custom.user.UserVerifiedException;
import com.blankfactor.auth.entity.User;
import com.blankfactor.auth.entity.dto.exp.RegisterResponse;
import com.blankfactor.auth.entity.dto.exp.VerifyResponse;
import com.blankfactor.auth.entity.dto.imp.RegisterRequest;
import com.blankfactor.auth.entity.dto.imp.VerifyRequest;
import com.blankfactor.auth.repository.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Log4j2
public class AuthService {

    private static final String USER_NOT_FOUND = "%s is not found";
    private static final String USER_FOUND = "%s is already in use";
    private static final String USER_ALREADY_VERIFIED = "%s is already verified";
    private static final String CODE_EXPIRED = "Verification code %s has expired";
    private static final String CODE_INCORRECT = "Incorrect verification code: %s";
    private static final String EMAIL_SUBJECT = "Account Verification";
    private static final String EMAIL_NOT_SENT = "Failed to send verification email to %s";
    private static final String PASSWORDS_DO_NOT_MATCH = "Passwords do not match. Please try again.";
    private static final String USER_NOT_VERIFIED = "Account is not verified!";
    private static final String INVALID_CREDENTIALS = "Incorrect username/email or password.";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final TemplateEngine templateEngine;
    private final ModelMapper modelMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Transactional
    public RegisterResponse register(RegisterRequest registerRequest) {
        log.debug("Registering user with email: {}", registerRequest.getEmail());
        validateCredentials(registerRequest);
        User user = User.builder()
                .email(registerRequest.getEmail())
                .password(this.passwordEncoder.encode(registerRequest.getPassword()))
                .username(registerRequest.getUsername())
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .birthDate(registerRequest.getBirthDate())
                .verificationCode(generateVerificationCode())
                .verificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15))
                .enabled(false)
                .roles(registerRequest.getRole().equals("attendee") ? Set.of(Role.ROLE_USER) : Set.of(Role.ROLE_USER, Role.ROLE_ADMIN))
                .build();
        this.userRepository.saveAndFlush(user);
        log.debug("User with email {} registered successfully", registerRequest.getEmail());
        sendVerificationEmail(user);
        return this.modelMapper.map(user, RegisterResponse.class);
    }

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

    @Transactional
    public VerifyResponse verify(VerifyRequest verifyRequest) {
        log.debug("Verifying user with email: {}", verifyRequest.getEmail());
        String userEmail = verifyRequest.getEmail();
        String userVerificationCode = verifyRequest.getVerificationCode();
        Optional<User> optionalUser = this.userRepository.findByEmail(userEmail);
        if (optionalUser.isEmpty()) {
            log.warn("User with email {} is not found", userEmail);
            throw new UserNotFoundException(String.format(USER_NOT_FOUND, userEmail));
        }
        User user = optionalUser.get();
        LocalDateTime verificationCodeExpiresAt = user.getVerificationCodeExpiresAt();
        if (user.isEnabled()) {
            log.warn("User with email {} is already verified", userEmail);
            throw new UserVerifiedException(String.format(USER_ALREADY_VERIFIED, userEmail));
        }
        if (!user.getVerificationCode().equals(userVerificationCode)) {
            log.warn("Incorrect verification code: {} for email: {}", userVerificationCode, userEmail);
            throw new IncorrectVerificationCodeException(String.format(CODE_INCORRECT, userVerificationCode));
        }
        if (verificationCodeExpiresAt.isBefore(LocalDateTime.now())) {
            log.warn("Verification code expired: {} for email: {}", userVerificationCode, userEmail);
            throw new ExpiredVerificationCodeException(String.format(CODE_EXPIRED, userVerificationCode));
        }
        user.setEnabled(true);
        user.setVerificationCode(null);
        user.setVerificationCodeExpiresAt(null);
        this.userRepository.saveAndFlush(user);
        log.debug("User with email {} verified successfully", userEmail);
        return this.modelMapper.map(user, VerifyResponse.class);
    }

    @Transactional
    public String resend(String email) {
        log.debug("Resending verification code to email: {}", email);
        Optional<User> optionalUser = this.userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            log.warn("User with email {} is not found", email);
            throw new UserNotFoundException(String.format(USER_NOT_FOUND, email));
        }
        User user = optionalUser.get();
        if (user.isEnabled()) {
            log.warn("User with email {} is already verified", email);
            throw new UserVerifiedException(String.format(USER_ALREADY_VERIFIED, email));
        }
        String newCode = generateVerificationCode();
        user.setVerificationCode(newCode);
        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));
        this.userRepository.saveAndFlush(user);
        sendVerificationEmail(user);
        log.debug("Verification code resent successfully to email: {}", email);
        return newCode;
    }

    public void sendVerificationEmail(User user) {
        try {
            this.emailService.sendVerificationEmail(
                    user.getEmail(),
                    EMAIL_SUBJECT,
                    htmlMessage(user.getUsername(), user.getVerificationCode()));
            log.debug("Verification email sent to: {}", user.getEmail());
        } catch (MessagingException e) {
            log.error("Failed to send verification email to: {}", user.getEmail());
            throw new VerificationEmailNotSentException(String.format(EMAIL_NOT_SENT, user.getEmail()), e);
        }
    }

    public void validateCredentials(RegisterRequest registerRequest) {
        log.debug("Validating credentials for email: {}", registerRequest.getEmail());
        Optional<User> byEmail = this.userRepository.findByEmail(registerRequest.getEmail());
        if (byEmail.isPresent()) {
            log.warn("Email {} is already in use", registerRequest.getEmail());
            throw new UserFoundException(String.format(USER_FOUND, registerRequest.getEmail()));
        }
        Optional<User> byUsername = this.userRepository.findByUsername(registerRequest.getUsername());
        if (byUsername.isPresent()) {
            log.warn("Username {} is already in use", registerRequest.getUsername());
            throw new UserFoundException(String.format(USER_FOUND, registerRequest.getUsername()));
        }
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            log.warn("Passwords do not match for email: {}", registerRequest.getEmail());
            throw new PasswordsDoNotMatchException(PASSWORDS_DO_NOT_MATCH);
        }
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        log.debug("Generated verification code: {}", code);
        return String.valueOf(code);
    }

    private String htmlMessage(String username, String code) {
        Context context = new Context();
        context.setVariable("username", username);
        context.setVariable("code", code);
        log.debug("Generated HTML verification message for user: {}", username);
        return templateEngine.process("verify-account-mail", context);
    }

    @Transactional
    public void resetPassword(String token, String newPassword, String confirmPassword) {
        log.info("Resetting password using token.");
        if (!newPassword.equals(confirmPassword)) {
            log.warn("Passwords do not match");
            throw new PasswordsDoNotMatchException("Passwords do not match. Please try again.");
        }

        String username = jwtService.extractUsername(token);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("User not found with username: {}", username);
                    return new UserNotFoundException(String.format("Username %s is not found", username));
                });

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.saveAndFlush(user);
        log.info("Password reset successfully for user: {}", username);
    }

    public void forgotPassword(String email) {
        log.info("Processing forgot password for email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                        log.warn("User not found with email: {}", email);
                        return new UserNotFoundException(String.format("Email %s is not found", email));
                        });
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("reset", true);
        String resetToken = jwtService.generateToken(extraClaims, user);

        //String resetLink = "https://localhost:8081/api/v1/auth/reset-password?token=" + resetToken;
        String resetLink = "http://localhost:5173/reset-password?token=" + resetToken;

        Context context = new Context();
        context.setVariable("username", user.getUsername());
        context.setVariable("resetLink", resetLink);

        String emailBody = templateEngine.process("reset-password-mail", context);

        try {
            emailService.sendResetPasswordEmail(user.getEmail(), "Reset Your Password", emailBody);
            log.info("Password reset email sent to: {}", email);
        } catch (MessagingException e) {
            log.error("Failed to send reset password email to: {}", email, e);
            throw new VerificationEmailNotSentException(String.format("Failed to send reset password email to %s", email), e);
        }
    }

}