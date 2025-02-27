package com.backend.service;

import com.backend.entity.dto.PaymentNotificationDTO;
import com.backend.service.notification.NotificationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.math.BigDecimal;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    private static final String DESTINATION = "/topic/notifications";
    private static final String BUYER_INFO = "buyer@example.com";
    private static final String TICKET_DETAILS = "VIP Ticket for Concert";
    private static final BigDecimal AMOUNT = new BigDecimal("100.50");

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Test
    void testSendPaymentNotification() {
        PaymentNotificationDTO notification = new PaymentNotificationDTO();
        notification.setAmount(AMOUNT);
        notification.setBuyerInfo(BUYER_INFO);
        notification.setTicketDetails(TICKET_DETAILS);

        notificationService.sendPaymentNotification(notification);

        verify(messagingTemplate, times(1))
                .convertAndSend(DESTINATION, notification);
    }
}


