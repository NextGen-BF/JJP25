package com.blankfactor.auth.controller;

import com.blankfactor.auth.entity.User;
import com.blankfactor.auth.entity.dto.exp.LoginResponse;
import com.blankfactor.auth.entity.dto.imp.LoginRequest;
import com.blankfactor.auth.service.JwtService;


public class AuthController {

    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginRequest loginRequest){
        log.info("Authenticating user: {}", loginRequest.getLoginIdentifier());
        User authenticatedUser = authService.login(loginRequest);
        String jwtToken = jwtService.generateToken(authenticatedUser);
        LoginResponse loginResponse = new LoginResponse(jwtToken, jwtService.getExpirationTime());
        log.debug("Generated JWT token for user: {}", loginRequest.getLoginIdentifier());
        return ResponseEntity.ok(loginResponse);
    }
}