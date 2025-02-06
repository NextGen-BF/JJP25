package com.blankfactor.auth.controller;

import com.blankfactor.auth.entity.dto.exp.RegisterResponse;
import com.blankfactor.auth.entity.dto.exp.VerifyResponse;
import com.blankfactor.auth.entity.dto.imp.RegisterRequest;
import com.blankfactor.auth.entity.dto.imp.VerifyRequest;
import com.blankfactor.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody @Valid RegisterRequest registerRequest) {
        return ResponseEntity.ok().body(this.authService.register(registerRequest));
    }

    @PostMapping("/verify")
    public ResponseEntity<VerifyResponse> verify(@RequestBody @Valid VerifyRequest verifyRequest) {
        return ResponseEntity.ok(this.authService.verifyUser(verifyRequest));
    }

    @PostMapping("/resend")
    public ResponseEntity<String> resend(@RequestParam String email) {
        return ResponseEntity.ok(String.format("Verification code resend successfully! New code: %s",
                this.authService.resendVerificationCode(email)));
    }

}