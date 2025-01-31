package com.backend.entity.ticket;

import com.backend.entity.BaseEntity;
import com.backend.entity.paymentExecution.PaymentExecution;
import com.backend.entity.user.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_tickets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserTicket extends BaseEntity {
    @OneToOne(mappedBy = "userTicket", cascade = CascadeType.ALL)
    private PaymentExecution paymentExecution;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @ManyToOne()
    @JoinColumn(name = "ticket_template_id", referencedColumnName = "id", nullable = false)
    private TicketTemplate ticketTemplate;

    @Column(name = "bought_at", nullable = false)
    private LocalDateTime boughtAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserTicket that = (UserTicket) o;
        return getId() != null && getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }

    @Override
    public String toString() {
        return "UserTicket{" +
                "id=" + getId() +
                ", user=" + (user != null ? user.getId() : null) +
                ", ticketTemplate=" + (ticketTemplate != null ? ticketTemplate.getId() : null) +
                ", boughtAt=" + boughtAt +
                '}';
    }
}