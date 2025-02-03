package com.backend.repository;

import com.backend.entity.ticket.TicketTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketTemplateRepository extends JpaRepository<TicketTemplate, Integer> {
}