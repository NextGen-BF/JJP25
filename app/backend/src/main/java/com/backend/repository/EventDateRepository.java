package com.backend.repository;

import com.backend.entity.event.EventDate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventDateRepository extends JpaRepository<EventDate, Integer> {
}