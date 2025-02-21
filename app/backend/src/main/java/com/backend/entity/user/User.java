package com.backend.entity.user;

import com.backend.entity.BaseEntity;
import com.backend.entity.feedback.Feedback;
import com.backend.entity.event.Event;
import com.backend.entity.notification.Notification;
import com.backend.entity.rsvp.RSVP;
import com.backend.entity.ticket.UserTicket;
import com.backend.entity.payment.Payment;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    private Long id;

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Payment> senderPayments = new ArrayList<>();

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Payment> receiverPayments = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<UserTicket> userTickets = new ArrayList<>();

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RSVP> sentRSVPs = new ArrayList<>();

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RSVP> receivedRSVPs = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Notification> notifications = new ArrayList<>();

    @OneToMany(mappedBy = "admin", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Event> events = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Feedback> feedbacks = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserType type;

    @Column(unique = true, length = 15)
    private String phone;

    @Column(length = 255)
    private String profilePicture;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

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
        User user = (User) o;
        return getId() != null && getId().equals(user.getId()) &&
                Objects.equals(phone, user.phone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), phone);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + getId() +
                ", phone='" + phone + '\'' +
                ", type=" + type +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

}