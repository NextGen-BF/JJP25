package com.backend.entity;

import com.backend.entity.paymentExecution.PaymentExecution;
import com.backend.entity.user.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "user_tickets")
public class UserTicket extends BaseEntity {
    @OneToOne(mappedBy = "id", cascade = CascadeType.ALL)
    PaymentExecution paymentExecution;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @ManyToOne()
    @JoinColumn(name = "ticket_template_id", referencedColumnName = "id", nullable = false)
    private TicketTemplate ticketTemplate;

    @Column(name = "bought_at", nullable = false)
    private LocalDateTime boughtAt;
}