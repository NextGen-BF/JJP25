package com.backend.entity;

import com.backend.entity.event.Event;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "event_statistics")
public class EventStatistics extends BaseEntity {
    @OneToOne()
    @JoinColumn(name = "event_id", referencedColumnName = "id", nullable = false)
    private Event event;
}