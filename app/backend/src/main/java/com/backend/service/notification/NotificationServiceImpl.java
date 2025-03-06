package com.backend.service.notification;

import com.backend.entity.dto.PaymentNotificationDTO;
import com.backend.entity.notification.Notification;
import com.backend.entity.notification.NotificationType;
import com.backend.entity.user.User;
import com.backend.exception.custom.NotificationDeliveryException;
import com.backend.repository.notification.NotificationRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class NotificationServiceImpl implements NotificationService {

    private static final String NOTIFICATION_DESTINATION = "/topic/notifications";

    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationRepository notificationRepository;

    public void sendPaymentNotification(PaymentNotificationDTO dto, User admin) {
        if (dto == null) {
            throw new NotificationDeliveryException("Notification cannot be null", null);
        }
        try {
            String description = "Payment of " + dto.getAmount() +
                    " received from " + dto.getBuyerInfo() +
                    ". Ticket details: " + dto.getTicketDetails();
            Notification notification = Notification.builder()
                    .user(admin)
                    .type(NotificationType.EVENT_DONATION)
                    .description(description)
                    .sentAt(LocalDateTime.now())
                    .build();
            notificationRepository.save(notification);

            messagingTemplate.convertAndSend(NOTIFICATION_DESTINATION, dto);
            log.info("Notification sent to {}: {}", NOTIFICATION_DESTINATION, dto);
        } catch (Exception e) {
            log.error("Failed to send payment notification: {}", dto, e);
            throw new NotificationDeliveryException("Failed to send payment notification", e);
        }
    }

    public List<Notification> getNotificationsForUser(Long userId) {
        return notificationRepository.findByUser_IdOrderBySentAtDesc(userId);
    }
}
