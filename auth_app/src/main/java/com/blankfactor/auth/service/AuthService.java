package com.blankfactor.auth.service;

import com.blankfactor.auth.exception.custom.*;
import com.blankfactor.auth.exception.custom.code.ExpiredVerificationCodeException;
import com.blankfactor.auth.exception.custom.code.IncorrectVerificationCodeException;
import com.blankfactor.auth.exception.custom.user.UserFoundException;
import com.blankfactor.auth.exception.custom.user.UserNotFoundException;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

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

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final TemplateEngine templateEngine;
    private final ModelMapper modelMapper;

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
                .build();
        this.userRepository.saveAndFlush(user);
        log.debug("User with email {} registered successfully", registerRequest.getEmail());
        sendVerificationEmail(user);
        return this.modelMapper.map(user, RegisterResponse.class);
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

}