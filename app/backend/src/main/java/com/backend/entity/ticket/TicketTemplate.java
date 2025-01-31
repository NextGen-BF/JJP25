package com.backend.entity.ticket;

import com.backend.entity.BaseEntity;
import com.backend.entity.event.Event;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "ticket_templates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketTemplate extends BaseEntity {
    @OneToMany(mappedBy = "ticketTemplate", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<UserTicket> userTickets = new ArrayList<>();

    @ManyToOne()
    @JoinColumn(name = "event_id", referencedColumnName = "id", nullable = false)
    private Event event;

    @Enumerated(EnumType.STRING)
    @Column(name = "ticket_type", nullable = false)
    private TicketType ticketType;

    @Enumerated(EnumType.STRING)
    @Column(name = "venue_type", nullable = false)
    private VenueType venueType;

    @Column(name = "price", nullable = false, precision = 4, scale = 2)
    private BigDecimal price;

    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "available_quantity", nullable = false)
    private int availableQuantity;

    @Column(name = "total_quantity", nullable = false)
    private int totalQuantity;

    @PrePersist
    @PreUpdate
    private void checkQuantities() {
        if (availableQuantity < 0) {
            throw new IllegalArgumentException("Available quantity cannot be negative");
        }
        if (totalQuantity < 0) {
            throw new IllegalArgumentException("Total quantity cannot be negative");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TicketTemplate that = (TicketTemplate) o;
        return getId() != null && getId().equals(that.getId()) &&
                Objects.equals(event, that.event) &&
                Objects.equals(price, that.price) &&
                ticketType == that.ticketType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), event, price, ticketType);
    }

    @Override
    public String toString() {
        return "TicketTemplate{" +
                "id=" + getId() +
                ", event=" + (event != null ? event.getId() : null) +
                ", ticketType=" + ticketType +
                ", venueType=" + venueType +
                ", price=" + price +
                ", eventDate=" + eventDate +
                ", availableQuantity=" + availableQuantity +
                ", totalQuantity=" + totalQuantity +
                ", description='" + description + '\'' +
                '}';
    }


}