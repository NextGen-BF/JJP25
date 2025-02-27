package com.backend.service.notification;

import com.backend.entity.dto.PaymentNotificationDTO;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private static final String NOTIFICATION_DESTINATION = "/topic/notifications";

    private final SimpMessagingTemplate messagingTemplate;

    public void sendPaymentNotification(PaymentNotificationDTO notification) {
        messagingTemplate.convertAndSend(NOTIFICATION_DESTINATION, notification);
    }
}
