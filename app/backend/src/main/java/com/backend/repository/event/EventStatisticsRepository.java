package com.backend.repository.event;

import com.backend.entity.event.EventStatistics;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventStatisticsRepository extends JpaRepository<EventStatistics, Integer> {
}