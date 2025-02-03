package com.backend.entity.event;

import com.backend.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "event_dates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventDate extends BaseEntity {
    @ManyToOne()
    @JoinColumn(name = "event_id", referencedColumnName = "id", nullable = false)
    private Event event;

    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventDate eventDate = (EventDate) o;
        return Objects.equals(getId(), eventDate.getId()) &&
                Objects.equals(event != null ? event.getId()
                        : null, eventDate.event != null ? eventDate.event.getId() : null) &&
                Objects.equals(date, eventDate.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), event != null ? event.getId() : null, date);
    }

    @Override
    public String toString() {
        return "EventDate{" +
                "id=" + getId() +
                ", event=" + (event != null ? event.getId() : null) +
                ", date=" + date +
                '}';
    }
}