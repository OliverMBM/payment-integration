package com.applicacorp.payments.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record PaymentRequest (

        @NotBlank(message = "id is required")
        @Pattern(regexp = "^[A-Za-z0-9_-]+$", message = "id contains invalid characters")
        String id,

        @NotBlank(message = "customerId is required")
        String customerId,

        @NotNull(message = "amount is required")
        @DecimalMin(value = "0.01", message = "amount must be greater than zero")
        BigDecimal amount,

        @NotBlank(message = "currency is required")
        @Pattern(regexp = "^[A-Z]{3}$", message = "currency must contain 3 uppercase letters")
        String currency,

        @NotNull(message = "timestamp is required")
        OffsetDateTime timestamp
        ) {}
