package com.blankfactor.auth.controller;


import com.blankfactor.auth.entity.User;
import com.blankfactor.auth.responses.LoginResponse;
import com.blankfactor.auth.entity.dto.exp.RegisterResponse;
import com.blankfactor.auth.entity.dto.exp.VerifyResponse;
import com.blankfactor.auth.entity.dto.imp.RegisterRequest;
import com.blankfactor.auth.entity.dto.imp.VerifyRequest;
import com.blankfactor.auth.model.dto.LoginUserDTO;
import com.blankfactor.auth.service.AuthService;
import com.blankfactor.auth.service.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody @Valid RegisterRequest registerRequest) {
        log.info("Register request received for email: {}", registerRequest.getEmail());
        RegisterResponse response = authService.register(registerRequest);
        log.info("User registered successfully with email: {}", registerRequest.getEmail());
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDTO loginUserDto){
        User authenticatedUser = authService.login(loginUserDto);
        String jwtToken = jwtService.generateToken(authenticatedUser);
        LoginResponse loginResponse = new LoginResponse(jwtToken, jwtService.getExpirationTime());
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/verify")
    public ResponseEntity<VerifyResponse> verify(@RequestBody @Valid VerifyRequest verifyRequest) {
        log.info("Verification request received for email: {}", verifyRequest.getEmail());
        VerifyResponse response = authService.verify(verifyRequest);
        log.info("User verified successfully with email: {}", verifyRequest.getEmail());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/resend")
    public ResponseEntity<String> resend(@RequestParam String email) {
        log.info("Resend verification code request received for email: {}", email);
        String newCode = authService.resendVerificationCode(email);
        log.info("Verification code {} resent successfully to email: {}", newCode, email);
        return ResponseEntity.ok(String.format("Verification code resent successfully! New code: %s", newCode));
    }
}