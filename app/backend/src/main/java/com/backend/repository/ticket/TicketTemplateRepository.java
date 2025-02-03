package com.backend.repository.ticket;

import com.backend.entity.ticket.TicketTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketTemplateRepository extends JpaRepository<TicketTemplate, Integer> {
}