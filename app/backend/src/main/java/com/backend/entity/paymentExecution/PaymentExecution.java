package com.backend.entity.paymentExecution;

import com.backend.entity.BaseEntity;
import com.backend.entity.ticket.UserTicket;
import com.backend.entity.payment.Payment;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "payment_executions")
public class PaymentExecution extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "payment_id", referencedColumnName = "id", nullable = false)
    private Payment payment;

    @OneToOne
    @JoinColumn(name = "user_ticket_id", referencedColumnName = "id", nullable = false)
    private UserTicket userTicket;

    @Column(name = "external_payment_status", nullable = false, length = 50)
    private String externalPaymentStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_execution", nullable = false)
    private PaymentExecution paymentExecution;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "error_code", length = 24)
    private String errorCode;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "refund_expiration_date", nullable = false)
    private LocalDateTime refundExpirationDate;

    @Column(name = "refunded_amount")
    private Integer refundedAmount;

    @Column(name = "refund_reason")
    private String refundReason;
}