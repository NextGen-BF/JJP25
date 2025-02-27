package com.blankfactor.auth.controller;

import com.blankfactor.auth.service.JwtService;
import com.blankfactor.auth.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class GoogleAuthController {

    private final UserService userService;

    private final JwtService jwtService;

    public GoogleAuthController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @GetMapping("/google")
    public ResponseEntity<String> googleAuth() {
        throw new IllegalStateException("Yet to be implemented");
    }



}
