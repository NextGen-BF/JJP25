package com.backend.entity.event;

import com.backend.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "event_statistics")
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
}