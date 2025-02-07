package com.blankfactor.auth.controller;

import com.blankfactor.auth.model.User;
import com.blankfactor.auth.model.dto.UserResponseDTO;
import com.blankfactor.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/users")
@RestController
@RequiredArgsConstructor
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    @GetMapping("/my-profile")
    public ResponseEntity<UserResponseDTO> getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        logger.info("Fetching authenticated user: {}", currentUser.getEmail());
        UserResponseDTO userResponseDTO = new UserResponseDTO(
                currentUser.getEmail(),
                currentUser.getFirstName(),
                currentUser.getLastName(),
                currentUser.getBirthDate()
        );
        logger.debug("UserResponseDTO: {}", userResponseDTO);
        return ResponseEntity.ok(userResponseDTO);
    }

}
