package com.blankfactor.auth.controller;

import com.blankfactor.auth.entity.User;
import com.blankfactor.auth.entity.dto.responses.LoginResponse;
import com.blankfactor.auth.entity.dto.responses.RegisterResponse;
import com.blankfactor.auth.entity.dto.responses.VerifyResponse;
import com.blankfactor.auth.entity.dto.requests.*;
import com.blankfactor.auth.service.AuthService;
import com.blankfactor.auth.service.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Log4j2
public class AuthController {

    private final AuthService authService;

    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody @Valid RegisterRequest registerRequest) {
        log.info("Register request received for email: {}", registerRequest.getEmail());
        RegisterResponse response = this.authService.register(registerRequest);
        log.info("User registered successfully with email: {}", registerRequest.getEmail());
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/verify")
    public ResponseEntity<VerifyResponse> verify(@RequestBody @Valid VerifyRequest verifyRequest) {
        log.info("Verification request received for email verification id: {}", verifyRequest.getUuid());
        VerifyResponse response = this.authService.verify(verifyRequest);
        log.info("User verified successfully with email verification id: {}", verifyRequest.getUuid());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/resend")
    public ResponseEntity<String> resend(@RequestParam String email) {
        log.info("Resend verification code request received for email: {}", email);
        String newCode = this.authService.resend(email);
        log.info("Verification code {} resent successfully to email: {}", newCode, email);
        return ResponseEntity.ok("Verification code resent successfully!");
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginRequest loginRequest){
        log.info("Authenticating user: {}", loginRequest.getLoginIdentifier());
        User authenticatedUser = authService.login(loginRequest);
        String jwtToken = jwtService.generateToken(authenticatedUser);
        LoginResponse loginResponse = new LoginResponse(jwtToken, jwtService.getExpirationTime());
        log.debug("Generated JWT token for user: {}", loginRequest.getLoginIdentifier());
        return ResponseEntity.ok(loginResponse);
    }


    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
        log.info("Forgot password request received for email: {}", request.getEmail());
        authService.forgotPassword(request.getEmail());
        return ResponseEntity.ok("Password reset link sent to your email");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        log.info("Reset password request received with token");
        authService.resetPassword(request.getToken(), request.getNewPassword(), request.getConfirmPassword());
        return ResponseEntity.ok("Password has been reset successfully");
    }

}
