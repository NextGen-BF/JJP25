package com.blankfactor.auth.service;

import com.blankfactor.auth.exception.custom.*;
import com.blankfactor.auth.model.User;
import com.blankfactor.auth.model.dto.exp.RegisterResponse;
import com.blankfactor.auth.model.dto.exp.VerifyResponse;
import com.blankfactor.auth.model.dto.imp.RegisterRequest;
import com.blankfactor.auth.model.dto.imp.VerifyRequest;
import com.blankfactor.auth.repository.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String USER_NOT_FOUND = "%s is not found!";
    private static final String USER_ALREADY_VERIFIED = "%s is already verified";
    private static final String CODE_EXPIRED = "Verification code %s has expired";
    private static final String CODE_INCORRECT = "Incorrect verification code: %s";
    private static final String EMAIL_SUBJECT = "Account Verification";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final TemplateEngine templateEngine;
    private final ModelMapper modelMapper;

    public RegisterResponse register(RegisterRequest registerRequest) {
        User user = User.builder()
                .email(registerRequest.getEmail())
                .password(this.passwordEncoder.encode(registerRequest.getPassword()))
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

    public VerifyResponse verifyUser(VerifyRequest verifyRequest) {
        String userEmail = verifyRequest.getEmail();
        String userVerificationCode = verifyRequest.getVerificationCode();
        Optional<User> optionalUser = this.userRepository.findByEmail(userEmail);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException(String.format(USER_NOT_FOUND, userEmail));
        }
        User user = optionalUser.get();
        LocalDateTime verificationCodeExpiresAt = user.getVerificationCodeExpiresAt();
        if (verificationCodeExpiresAt == null) {
            throw new NullVerificationCodeException(String.format(USER_ALREADY_VERIFIED, userEmail));
        }
        if (verificationCodeExpiresAt.isBefore(LocalDateTime.now())) {
            throw new ExpiredVerificationCodeException(String.format(CODE_EXPIRED, userVerificationCode));
        }
        if (!user.getVerificationCode().equals(userVerificationCode)) {
            throw new IncorrectVerificationCodeException(String.format(CODE_INCORRECT, userVerificationCode));
        }
        user.setEnabled(true);
        user.setVerificationCode(null);
        user.setVerificationCodeExpiresAt(null);
        this.userRepository.saveAndFlush(user);
        return this.modelMapper.map(user, VerifyResponse.class);
    }

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
        sendVerificationEmail(user);
        this.userRepository.saveAndFlush(user);
        return newCode;
    }

    public void sendVerificationEmail(User user) {
        try {
            this.emailService.sendVerificationEmail(
                    user.getEmail(),
                    EMAIL_SUBJECT,
                    htmlMessage(user.getUsername(), user.getVerificationCode()));
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public String generateVerificationCode() {
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