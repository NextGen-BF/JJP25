package com.blankfactor.auth.controller;

import com.blankfactor.auth.entity.User;
import com.blankfactor.auth.entity.dto.response.UserResponse;
import com.blankfactor.auth.entity.dto.imp.AssignRoleRequest;
import com.blankfactor.auth.service.AuthService;
import com.blankfactor.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/users")
@RestController
@RequiredArgsConstructor
@Log4j2
public class UserController {

    private final UserService userService;
    private final AuthService authService;

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

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/assign-role")
    public ResponseEntity<String> assignRole(@RequestBody AssignRoleRequest request) {
        log.info("Assigning role request received for user with id: {}", request.getUserId());
        authService.assignAdminRole(request.getUserId(), request.getRole());
        return ResponseEntity.ok("Role assigned successfully");
    }

}