package com.blankfactor.auth.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @Mock
    private MimeMessage mimeMessage;

    @BeforeEach
    void setUp() {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    @Test
    void shouldSuccessfullySendAnEmail() throws MessagingException {
        // Given
        String to = "test@example.com";
        String subject = "Verify your email";
        String body = "<p>Please verify your email by clicking the link below.</p>";

        // When
        this.emailService.sendVerificationEmail(to, subject, body);

        // Then
        verify(this.mailSender, times(1)).createMimeMessage();
        verify(this.mailSender, times(1)).send(this.mimeMessage);
    }

    @Test
    void shouldThrowMessagingExceptionWhenEmailIsNotSent() {
        // Given
        String to = "test@example.com";
        String subject = "Verify your email";
        String body = "<p>Please verify your email.</p>";

        // When
        doThrow(new RuntimeException(new MessagingException("Email sending failed")))
                .when(this.mailSender).send(this.mimeMessage);

        // Then
        Exception exception = assertThrows(RuntimeException.class,
                () -> this.emailService.sendVerificationEmail(to, subject, body));
        assertTrue(exception.getCause() instanceof MessagingException);
        assertEquals("Email sending failed", exception.getCause().getMessage());
        verify(this.mailSender, times(1)).createMimeMessage();
        verify(this.mailSender, times(1)).send(this.mimeMessage);
    }
}
