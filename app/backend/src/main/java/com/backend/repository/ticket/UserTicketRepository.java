package com.backend.repository.ticket;

import com.backend.entity.ticket.UserTicket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTicketRepository extends JpaRepository<UserTicket, Integer> {
}