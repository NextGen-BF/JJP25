package com.blankfactor.auth.controller;

import com.blankfactor.auth.entity.User;
import com.blankfactor.auth.entity.dto.responses.UserResponse;
import com.blankfactor.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/users")
@RestController
@RequiredArgsConstructor
@Log4j2
public class UserController {

    private final UserService userService;

    @GetMapping("/user-profile")
    public ResponseEntity<UserResponse> getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        log.info("Fetching authenticated user: {}", currentUser.getEmail());
        UserResponse userResponse = new UserResponse(
                currentUser.getEmail(),
                currentUser.getUsername(),
                currentUser.getFirstName(),
                currentUser.getLastName(),
                currentUser.getBirthDate()
        );
        log.debug("UserResponseDTO: {}", userResponse);
        return ResponseEntity.ok(userResponse);
    }

}