package com.backend.repository;

import com.backend.entity.ticket.UserTicket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTicketRepository extends JpaRepository<UserTicket, Integer> {
}