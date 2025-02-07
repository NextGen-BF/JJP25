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
        sendVerificationEmail(user);
        return this.modelMapper.map(user, RegisterResponse.class);
    }

    @Transactional
    public VerifyResponse verifyUser(VerifyRequest verifyRequest) {
        String userEmail = verifyRequest.getEmail();
        String userVerificationCode = verifyRequest.getVerificationCode();
        Optional<User> optionalUser = this.userRepository.findByEmail(userEmail);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException(String.format(USER_NOT_FOUND, userEmail));
        }
        User user = optionalUser.get();
        LocalDateTime verificationCodeExpiresAt = user.getVerificationCodeExpiresAt();
        if (user.isEnabled()) {
            throw new UserVerifiedException(String.format(USER_ALREADY_VERIFIED, userEmail));
        }
        if (!user.getVerificationCode().equals(userVerificationCode)) {
            throw new IncorrectVerificationCodeException(String.format(CODE_INCORRECT, userVerificationCode));
        }
        if (verificationCodeExpiresAt.isBefore(LocalDateTime.now())) {
            throw new ExpiredVerificationCodeException(String.format(CODE_EXPIRED, userVerificationCode));
        }
        user.setEnabled(true);
        user.setVerificationCode(null);
        user.setVerificationCodeExpiresAt(null);
        this.userRepository.saveAndFlush(user);
        return this.modelMapper.map(user, VerifyResponse.class);
    }

    @Transactional
    public String resendVerificationCode(String email) {
        Optional<User> optionalUser = this.userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException(String.format(USER_NOT_FOUND, email));
        }
        User user = optionalUser.get();
        if (user.isEnabled()) {
            throw new UserVerifiedException(String.format(USER_ALREADY_VERIFIED, email));
        }
        String newCode = generateVerificationCode();
        user.setVerificationCode(newCode);
        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));
        this.userRepository.saveAndFlush(user);
        sendVerificationEmail(user);
        return newCode;
    }

    public void sendVerificationEmail(User user) {
        try {
            this.emailService.sendVerificationEmail(
                    user.getEmail(),
                    EMAIL_SUBJECT,
                    htmlMessage(user.getUsername(), user.getVerificationCode()));
        } catch (MessagingException e) {
            throw new VerificationEmailNotSentException(String.format(EMAIL_NOT_SENT, user.getEmail()), e);
        }
    }

    private void validateCredentials(RegisterRequest registerRequest) {
        Optional<User> byEmail = this.userRepository.findByEmail(registerRequest.getEmail());
        if (byEmail.isPresent()) {
            throw new UserFoundException(String.format(USER_FOUND, registerRequest.getEmail()));
        }
        Optional<User> byUsername = this.userRepository.findByUsername(registerRequest.getUsername());
        if (byUsername.isPresent()) {
            throw new UserFoundException(String.format(USER_FOUND, registerRequest.getUsername()));
        }
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            throw new PasswordsDoNotMatchException(PASSWORDS_DO_NOT_MATCH);
        }
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }

    private String htmlMessage(String username, String code) {
        Context context = new Context();
        context.setVariable("username", username);
        context.setVariable("code", code);
        return templateEngine.process("verify-account-mail", context);
    }

}