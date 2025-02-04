package com.blankfactor.auth.controller;

import com.blankfactor.auth.model.User;
import com.blankfactor.auth.model.dto.RegisterRequest;
import com.blankfactor.auth.model.dto.VerifiedUserDTO;
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
    public ResponseEntity<User> register(@RequestBody @Valid RegisterRequest registerRequest) {
        return ResponseEntity.ok().body(this.authService.register(registerRequest));
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verify(@RequestBody @Valid VerifiedUserDTO verifiedUserDTO) {
        try {
            this.authService.verifyUser(verifiedUserDTO);
            return ResponseEntity.ok("Account verified successfully!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/resend")
    public ResponseEntity<?> resend(@RequestParam String email) {
        try {
            this.authService.resendVerificationCode(email);
            return ResponseEntity.ok().body("Verification code resend successfully!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}