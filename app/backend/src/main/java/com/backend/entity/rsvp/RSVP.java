package com.backend.entity.rsvp;

import com.backend.entity.BaseEntity;
import com.backend.entity.event.Event;
import com.backend.entity.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "rsvp_invitations")
public class RSVP extends BaseEntity {
    @ManyToOne()
    @JoinColumn(name = "sender_id", referencedColumnName = "id", nullable = false)
    private User sender;

    @ManyToOne()
    @JoinColumn(name = "receiver_id", referencedColumnName = "id", nullable = false)
    private User receiver;

    @ManyToOne()
    @JoinColumn(name = "event_id", referencedColumnName = "id", nullable = false)
    private Event event;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RSVPStatus status;

    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;

    @Column(name = "expiration_date", nullable = false)
    private LocalDateTime expirationDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RSVP rsvp = (RSVP) o;
        return getId() != null && getId().equals(rsvp.getId());
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }

    @Override
    public String toString() {
        return "RSVP{" +
                "id=" + getId() +
                ", sender=" + (sender != null ? sender.getId() : null) +
                ", receiver=" + (receiver != null ? receiver.getId() : null) +
                ", event=" + (event != null ? event.getId() : null) +
                ", status=" + status +
                ", sentAt=" + sentAt +
                ", expirationDate=" + expirationDate +
                '}';
    }
}