package com.blankfactor.auth.service;

import com.blankfactor.auth.model.User;
import com.blankfactor.auth.model.dto.RegisterRequest;
import com.blankfactor.auth.model.dto.VerifiedUserDTO;
import com.blankfactor.auth.repository.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
        sendVerificationEmail(user);
        return this.userRepository.saveAndFlush(user);
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
        return
                "<!DOCTYPE html>" +
                        "<html>" +
                        "<head>" +
                        "<style>" +
                        "body { font-family: Arial, sans-serif; background-color: #f4f4f9; color: #333; }" +
                        ".email-container { width: 100%; max-width: 600px; margin: 0 auto; padding: 20px; background-color: #ffffff; border: 1px solid #ddd; border-radius: 8px; }" +
                        ".header { text-align: center; padding: 10px 0; }" +
                        ".header h1 { color: #4CAF50; }" +
                        ".content { padding: 20px; line-height: 1.6; }" +
                        ".code-container { text-align: center; font-size: 24px; font-weight: bold; padding: 15px; background-color: #f9f9f9; border: 1px dashed #4CAF50; margin-top: 20px; border-radius: 5px; color: #333; }" +
                        ".footer { text-align: center; font-size: 12px; color: #888; padding-top: 20px; }" +
                        "</style>" +
                        "</head>" +
                        "<body>" +
                        "<div class='email-container'>" +
                        "    <div class='header'>" +
                        "        <h1>Welcome to Our Service!</h1>" +
                        "    </div>" +
                        "    <div class='content'>" +
                        "        <p>Hi " + username + ",</p>" +
                        "        <p>Thank you for registering with us! Please use the following code to verify your email address:</p>" +
                        "    </div>" +
                        "    <div class='code-container'>" +
                        "        " + code +
                        "    </div>" +
                        "    <div class='content'>" +
                        "        <p>If you did not sign up for an account, please ignore this email.</p>" +
                        "    </div>" +
                        "    <div class='footer'>" +
                        "        <p>&copy; 2024 Your Company Name. All rights reserved.</p>" +
                        "    </div>" +
                        "</div>" +
                        "</body>" +
                        "</html>";
    }

}