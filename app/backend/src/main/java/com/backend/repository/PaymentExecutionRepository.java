package com.backend.repository;

import com.backend.entity.paymentExecution.PaymentExecution;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentExecutionRepository extends JpaRepository<PaymentExecution, Integer> {
}