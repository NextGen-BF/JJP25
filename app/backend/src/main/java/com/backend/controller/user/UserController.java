package com.backend.controller.user;

import com.backend.entity.dto.exp.RegisterResponse;
import com.backend.entity.dto.imp.RegisterRequest;
import com.backend.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Log4j2
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody @Valid RegisterRequest registerRequest) {
        log.info("Register request received for id: {}", registerRequest.getId());
        RegisterResponse registerResponse = this.userService.register(registerRequest);
        log.info("User registered successfully with id: {}", registerRequest.getId());
        return ResponseEntity.ok().body(registerResponse);
    }

}
