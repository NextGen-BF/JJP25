package com.backend.entity;

import com.backend.entity.event.Event;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "venues")
public class Venue extends BaseEntity {
    @ManyToOne()
    @JoinColumn(name = "event_id", referencedColumnName = "id", nullable = false)
    private Event event;
}