package com.applicacorp.payments.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record PaymentResponse(
        String id,
        String customerId,
        BigDecimal amount,
        String currency,
        OffsetDateTime timestamp,
        String status
) {
}
