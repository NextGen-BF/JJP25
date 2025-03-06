package com.backend.service.unit;

import com.backend.entity.dto.PaymentNotificationDTO;
import com.backend.entity.user.User;
import com.backend.exception.custom.NotificationDeliveryException;
import com.backend.repository.notification.NotificationRepository;
import com.backend.service.notification.NotificationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    private static final String DESTINATION = "/topic/notifications";
    private static final String BUYER_INFO = "buyer@example.com";
    private static final String TICKET_DETAILS = "VIP Ticket for Concert";
    private static final BigDecimal AMOUNT = new BigDecimal("100.50");

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private User dummyAdmin() {
        User admin = new User();
        admin.setId(1L);
        return admin;
    }

    @Test
    void testSendPaymentNotification() {
        PaymentNotificationDTO notification = new PaymentNotificationDTO();
        notification.setAmount(AMOUNT);
        notification.setBuyerInfo(BUYER_INFO);
        notification.setTicketDetails(TICKET_DETAILS);
        User admin = dummyAdmin();

        notificationService.sendPaymentNotification(notification, admin);

        verify(messagingTemplate, times(1))
                .convertAndSend(DESTINATION, notification);
    }

    @Test
    void testSendPaymentNotificationThrowsNotificationDeliveryException() {
        PaymentNotificationDTO notification = new PaymentNotificationDTO();
        notification.setAmount(AMOUNT);
        notification.setBuyerInfo(BUYER_INFO);
        notification.setTicketDetails(TICKET_DETAILS);
        User admin = dummyAdmin();

        RuntimeException runtimeException = new RuntimeException("Messaging failure");
        doThrow(runtimeException).when(messagingTemplate).convertAndSend(DESTINATION, notification);

        NotificationDeliveryException thrown = assertThrows(NotificationDeliveryException.class, () ->
                notificationService.sendPaymentNotification(notification, admin)
        );
        assertEquals("Failed to send payment notification", thrown.getMessage());
        assertEquals(runtimeException, thrown.getCause());
    }

    @Test
    void testSendPaymentNotificationWithNullNotification() {
        User admin = dummyAdmin();

        NotificationDeliveryException thrown = assertThrows(NotificationDeliveryException.class, () ->
                notificationService.sendPaymentNotification(null, admin)
        );
        assertEquals("Notification cannot be null", thrown.getMessage());
    }
}



