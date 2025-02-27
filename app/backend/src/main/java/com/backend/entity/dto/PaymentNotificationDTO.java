package com.backend.entity.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PaymentNotificationDTO {
    private BigDecimal amount;
    private String buyerInfo;
    private String ticketDetails;
}

