package com.backend.repository.rsvp;

import com.backend.entity.rsvp.RSVP;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RSVPRepository extends JpaRepository<RSVP, Integer> {
}