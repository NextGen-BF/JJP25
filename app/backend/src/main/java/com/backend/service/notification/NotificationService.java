package com.backend.service.notification;

import com.backend.entity.dto.PaymentNotificationDTO;
import com.backend.entity.notification.Notification;
import com.backend.entity.user.User;

import java.util.List;

public interface NotificationService {
    public void sendPaymentNotification(PaymentNotificationDTO notification, User admin);
    List<Notification> getNotificationsForUser(Long userId);
}
