package com.backend.entity.event;

import com.backend.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "event_statistics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventStatistics extends BaseEntity {
    @OneToOne()
    @JoinColumn(name = "event_id", referencedColumnName = "id", nullable = false)
    private Event event;

    @Column(name = "total_tickets_sold", nullable = false)
    private Integer totalTicketsSold = 0;

    @Column(name = "total_revenue", nullable = false, columnDefinition = "decimal(6,2)")
    private BigDecimal totalRevenue = BigDecimal.ZERO;

    @Column(name = "average_rating", nullable = false, columnDefinition = "decimal(1,1)")
    private BigDecimal averageRating = BigDecimal.ZERO;

    @Column(name = "feedback_count", nullable = false)
    private Integer feedbackCount  = 0;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventStatistics that = (EventStatistics) o;
        return Objects.equals(getId(), that.getId()) &&
                Objects.equals(event != null ? event.getId() : null, that.event != null ? that.event.getId() : null) &&
                Objects.equals(totalRevenue, that.totalRevenue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), event != null ? event.getId() : null, totalRevenue);
    }

    @Override
    public String toString() {
        return "EventStatistics{" +
                "id=" + getId() +
                ", event=" + (event != null ? event.getId() : null) +  // Avoid lazy loading in toString
                ", totalTicketsSold=" + totalTicketsSold +
                ", totalRevenue=" + totalRevenue +
                ", averageRating=" + averageRating +
                ", feedbackCount=" + feedbackCount +
                '}';
    }
}