package com.backend.service.notification;

import com.backend.entity.dto.PaymentNotificationDTO;

public interface NotificationService {
    public void sendPaymentNotification(PaymentNotificationDTO notification);
}
