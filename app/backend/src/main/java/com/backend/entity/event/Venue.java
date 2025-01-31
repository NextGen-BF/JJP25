package com.backend.entity.event;

import com.backend.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "venues")
public class Venue extends BaseEntity {
    @ManyToMany(mappedBy = "venues")
    private List<Event> events = new ArrayList<>();
}