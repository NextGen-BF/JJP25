package com.blankfactor.auth.service;

import com.blankfactor.auth.entity.EmailVerification;
import com.blankfactor.auth.entity.Role;
import com.blankfactor.auth.entity.dto.requests.InformRequest;
import com.blankfactor.auth.entity.dto.requests.LoginRequest;
import com.blankfactor.auth.exception.custom.*;
import com.blankfactor.auth.exception.custom.code.ExpiredVerificationCodeException;
import com.blankfactor.auth.exception.custom.code.IncorrectVerificationCodeException;
import com.blankfactor.auth.exception.custom.credentials.InvalidCredentialsException;
import com.blankfactor.auth.exception.custom.credentials.PasswordsDoNotMatchException;
import com.blankfactor.auth.exception.custom.email.VerificationEmailNotSentException;
import com.blankfactor.auth.exception.custom.user.*;
import com.blankfactor.auth.entity.User;
import com.blankfactor.auth.entity.dto.responses.RegisterResponse;
import com.blankfactor.auth.entity.dto.responses.VerifyResponse;
import com.blankfactor.auth.entity.dto.requests.RegisterRequest;
import com.blankfactor.auth.entity.dto.requests.VerifyRequest;
import com.blankfactor.auth.exception.custom.email.EmailVerificationNotFound;
import com.blankfactor.auth.repository.EmailVerificationRepository;
import com.blankfactor.auth.repository.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Log4j2
public class AuthService {

    private static final String USER_NOT_FOUND = "%s is not found";
    private static final String VERIFICATION_NOT_FOUND = "%s is not found";
    private static final String USER_FOUND = "%s is already in use";
    private static final String USER_EXISTS = "User with id: %s already exists";
    private static final String USER_NOT_AUTHENTICATED = "The request might not have token or has an expired one";
    private static final String USER_ALREADY_VERIFIED = "%s is already verified";
    private static final String CODE_EXPIRED = "Verification code %s has expired";
    private static final String CODE_INCORRECT = "Incorrect verification code: %s";
    private static final String EMAIL_SUBJECT = "Account Verification";
    private static final String EMAIL_NOT_SENT = "Failed to send verification email to %s";
    private static final String PASSWORDS_DO_NOT_MATCH = "Passwords do not match. Please try again.";
    private static final String USER_NOT_VERIFIED = "Account is not verified!";
    private static final String INVALID_CREDENTIALS = "Incorrect username/email or password.";
    private static final String TOKEN_PARAM_STRING = "?token=";
    private static final String SERVICE_UNAVAILABLE = "Sorry, verification is not possible at the moment";

    @Value("${app.reset-password.url}")
    private String resetPasswordBaseUrl;

    @Value("${app.register.endpoint}")
    private String registerEndpoint;

    private final UserRepository userRepository;
    private final EmailVerificationRepository emailVerificationRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final TemplateEngine templateEngine;
    private final ModelMapper modelMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RestClient restClient;

    @Transactional
    public RegisterResponse register(RegisterRequest registerRequest) {
        log.debug("Registering user with email: {}", registerRequest.getEmail());
        validateCredentials(registerRequest);
        EmailVerification emailVerification = this.emailVerificationRepository.saveAndFlush(EmailVerification.builder()
                .uuid(UUID.randomUUID().toString())
                .code(generateVerificationCode())
                .codeExpirationDate(LocalDateTime.now().plusMinutes(15))
                .user(null)
                .build());
        User user = this.userRepository.saveAndFlush(User.builder()
                .email(registerRequest.getEmail())
                .password(this.passwordEncoder.encode(registerRequest.getPassword()))
                .username(registerRequest.getUsername())
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .birthDate(registerRequest.getBirthDate())
                .enabled(false)
                .emailVerification(emailVerification)
                .roles(registerRequest.getRole().equals("attendee") ? Set.of(Role.ROLE_USER) : Set.of(Role.ROLE_USER, Role.ROLE_ADMIN))
                .build());
        emailVerification.setUser(user);
        this.emailVerificationRepository.saveAndFlush(emailVerification);
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
        log.debug("Verifying user with email verification id: {}", verifyRequest.getUuid());
        String requestUuid = verifyRequest.getUuid();
        String requestCode = verifyRequest.getCode();
        Optional<EmailVerification> optionalEmailVerification = this.emailVerificationRepository.findByUuid(requestUuid);
        if (optionalEmailVerification.isEmpty()) {
            log.warn("Email verification with id {} is not found", requestUuid);
            throw new EmailVerificationNotFound(String.format(VERIFICATION_NOT_FOUND, requestUuid));
        }
        EmailVerification emailVerification = optionalEmailVerification.get();
        User user = emailVerification.getUser();
        String userEmail = user.getEmail();
        if (user.isEnabled()) {
            log.warn("User with email {} is already verified", userEmail);
            throw new UserVerifiedException(String.format(USER_ALREADY_VERIFIED, userEmail));
        }
        if (!emailVerification.getCode().equals(requestCode)) {
            log.warn("Incorrect verification code: {} for email: {}", requestCode, userEmail);
            throw new IncorrectVerificationCodeException(String.format(CODE_INCORRECT, requestCode));
        }
        if (emailVerification.getCodeExpirationDate().isBefore(LocalDateTime.now())) {
            log.warn("Verification code expired: {} for email: {}", requestCode, userEmail);
            throw new ExpiredVerificationCodeException(String.format(CODE_EXPIRED, requestCode));
        }
        user.setEnabled(true);
        this.userRepository.saveAndFlush(user);
        emailVerification.setCode(null);
        emailVerification.setCodeExpirationDate(null);
        this.emailVerificationRepository.saveAndFlush(emailVerification);
        log.debug("User with email {} verified successfully", userEmail);
        // informEmsApp(user.getId(), user.getAuthorities().size() == 2 ? "ORGANISER" : "ATTENDEE", this.jwtService.generateToken(user));
        log.debug("ems_app was successfully informed about the creation of user {}", userEmail);
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
        EmailVerification emailVerification = user.getEmailVerification();
        emailVerification.setCode(newCode);
        emailVerification.setCodeExpirationDate(LocalDateTime.now().plusMinutes(15));
        this.emailVerificationRepository.saveAndFlush(emailVerification);
        sendVerificationEmail(user);
        log.debug("Verification code resent successfully to email: {}", email);
        return newCode;
    }

    public void sendVerificationEmail(User user) {
        try {
            this.emailService.sendVerificationEmail(
                    user.getEmail(),
                    EMAIL_SUBJECT,
                    htmlMessage(user.getUsername(), user.getEmailVerification().getCode()));
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
            throw new PasswordsDoNotMatchException(PASSWORDS_DO_NOT_MATCH);
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

        String resetLink = resetPasswordBaseUrl + TOKEN_PARAM_STRING + resetToken;

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

    private void informEmsApp(Long id, String role, String token) {
        log.debug("Sending { \"id\": \"{}\", \"role:\" \"{}\" } to ems_app backend", id, role);
        try {
            this.restClient
                    .post()
                    .uri(registerEndpoint)
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(InformRequest.builder()
                            .id(id)
                            .role(role.toUpperCase())
                            .build())
                    .exchange((request, response) -> {
                        if (response.getStatusCode().isSameCodeAs(HttpStatus.FORBIDDEN)) {
                            log.error("The request was forbidden due to missing or expired token.");
                            throw new InvalidInformRequestException(USER_NOT_AUTHENTICATED);
                        }
                        if (response.getStatusCode().isSameCodeAs(HttpStatus.CONFLICT)) {
                            log.error("The request faced a conflict due to the existence of user with id: {}", id);
                            throw new UserExistsException(String.format(USER_EXISTS, id));
                        }
                        return true;
                    });
        } catch (ResourceAccessException e) {
            throw new ServiceUnavailableException(SERVICE_UNAVAILABLE);
        }
    }

}