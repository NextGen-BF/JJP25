package com.blankfactor.auth.service;

import com.blankfactor.auth.model.User;
import com.blankfactor.auth.model.dto.RegisterRequest;
import com.blankfactor.auth.model.dto.VerifiedUserDTO;
import com.blankfactor.auth.repository.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
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

    private static final String CODE_NULL = "Verification code is null! You might have already verified your account!";
    private static final String CODE_EXPIRED = "Verification code has expired!";
    private static final String CODE_INCORRECT = "Incorrect verification code!";
    private static final String ACCOUNT_ALREADY_VERIFIED = "Account is already verified!";
    private static final String USER_NOT_FOUND = "User not found!";
    private static final String EMAIL_SUBJECT = "Account Verification";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final TemplateEngine templateEngine;

    public User register(RegisterRequest registerRequest) {
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
        User newUser = this.userRepository.saveAndFlush(user);
        sendVerificationEmail(user);
        return newUser;
    }

    public void verifyUser(VerifiedUserDTO verifiedUserDTO) {
        Optional<User> optionalUser = this.userRepository.findByEmail(verifiedUserDTO.getEmail());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            LocalDateTime verificationCodeExpiresAt = user.getVerificationCodeExpiresAt();
            if (verificationCodeExpiresAt == null) {
                throw new RuntimeException(CODE_NULL);
            }
            if (verificationCodeExpiresAt.isBefore(LocalDateTime.now())) {
                throw new RuntimeException(CODE_EXPIRED);
            }
            if (user.getVerificationCode().equals(verifiedUserDTO.getVerificationCode())) {
                user.setEnabled(true);
                user.setVerificationCode(null);
                user.setVerificationCodeExpiresAt(null);
                this.userRepository.saveAndFlush(user);
            } else {
                throw new RuntimeException(CODE_INCORRECT);
            }
        } else {
            throw new RuntimeException(USER_NOT_FOUND);
        }
    }

    public void resendVerificationCode(String email) {
        Optional<User> optionalUser = this.userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.isEnabled()) {
                throw new RuntimeException(ACCOUNT_ALREADY_VERIFIED);
            }
            user.setVerificationCode(generateVerificationCode());
            user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));
            sendVerificationEmail(user);
            this.userRepository.saveAndFlush(user);
        } else {
            throw new RuntimeException(USER_NOT_FOUND);
        }
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