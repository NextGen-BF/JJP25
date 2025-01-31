package com.backend.entity.event;

import com.backend.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "venues")
public class Venue extends BaseEntity {
    @ManyToMany(mappedBy = "venues")
    private List<Event> events;
}