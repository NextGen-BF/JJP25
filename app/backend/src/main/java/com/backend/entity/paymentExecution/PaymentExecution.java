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
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_executions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
    @Column(name = "action_type", nullable = false)
    private ActionType actionType;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "error_code", length = 40)
    private String errorCode;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "refund_expiration_date", nullable = false)
    private LocalDateTime refundExpirationDate;

    @Column(name = "refunded_amount", precision = 4, scale = 2)
    private BigDecimal refundedAmount;

    @Column(name = "refund_reason")
    private String refundReason;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentExecution that = (PaymentExecution) o;
        return getId() != null && getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }

    @Override
    public String toString() {
        return "PaymentExecution{" +
                "id=" + getId() +
                ", externalPaymentStatus='" + externalPaymentStatus + '\'' +
                ", actionType=" + actionType +
                ", description='" + description + '\'' +
                ", refundExpirationDate=" + refundExpirationDate +
                ", refundedAmount=" + refundedAmount +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}