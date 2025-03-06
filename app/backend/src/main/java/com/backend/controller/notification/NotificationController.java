package com.backend.controller.notification;

import com.backend.entity.dto.PaymentNotificationDTO;
import com.backend.entity.notification.Notification;
import com.backend.entity.user.User;
import com.backend.repository.user.UserRepository;
import com.backend.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<Notification>> getNotifications(Authentication authentication) {
        Long adminId = extractUserId(authentication);
        List<Notification> notifications = notificationService.getNotificationsForUser(adminId);
        return ResponseEntity.ok(notifications);
    }

    private Long extractUserId(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof Long) {
            return (Long) principal;
        } else if (principal instanceof String) {
            try {
                return Long.parseLong((String) principal);
            } catch (NumberFormatException e) {
                throw new IllegalStateException("Invalid principal format", e);
            }
        }
        throw new IllegalStateException("Unable to extract user id from authentication");
    }



    @PostMapping("/send")
    public ResponseEntity<String> sendTestNotification(@RequestBody PaymentNotificationDTO notification) {
        User admin = userRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Admin user not found"));
        notificationService.sendPaymentNotification(notification, admin);
        return ResponseEntity.ok("Notification sent");
    }

}
