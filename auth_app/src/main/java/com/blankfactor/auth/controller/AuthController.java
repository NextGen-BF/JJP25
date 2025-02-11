package com.blankfactor.auth.controller;

import com.blankfactor.auth.entity.dto.exp.RegisterResponse;
import com.blankfactor.auth.entity.dto.exp.VerifyResponse;
import com.blankfactor.auth.entity.dto.imp.RegisterRequest;
import com.blankfactor.auth.entity.dto.imp.VerifyRequest;
import com.blankfactor.auth.service.AuthService;
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

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody @Valid RegisterRequest registerRequest) {
        log.info("Register request received for email: {}", registerRequest.getEmail());
        RegisterResponse response = this.authService.register(registerRequest);
        log.info("User registered successfully with email: {}", registerRequest.getEmail());
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/verify")
    public ResponseEntity<VerifyResponse> verify(@RequestBody @Valid VerifyRequest verifyRequest) {
        log.info("Verification request received for email: {}", verifyRequest.getEmail());
        VerifyResponse response = this.authService.verify(verifyRequest);
        log.info("User verified successfully with email: {}", verifyRequest.getEmail());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/resend")
    public ResponseEntity<String> resend(@RequestParam String email) {
        log.info("Resend verification code request received for email: {}", email);
        String newCode = this.authService.resendVerificationCode(email);
        log.info("Verification code {} resent successfully to email: {}", newCode, email);
        return ResponseEntity.ok(String.format("Verification code resent successfully! New code: %s", newCode));
    }

}
