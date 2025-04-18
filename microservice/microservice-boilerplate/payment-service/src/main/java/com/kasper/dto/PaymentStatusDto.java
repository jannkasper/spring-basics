package com.kasper.dto;

import java.time.LocalDateTime;
import java.util.Objects;

// Using record for DTO as per instructions
public record PaymentStatusDto(
        String orderId,
        String status, // e.g., "PAID", "PENDING", "FAILED"
        LocalDateTime timestamp
) {
    // Compact canonical constructor for validation
    public PaymentStatusDto {
        Objects.requireNonNull(orderId, "Order ID cannot be null");
        if (orderId.isBlank()) {
            throw new IllegalArgumentException("Order ID cannot be blank.");
        }
        Objects.requireNonNull(status, "Payment status cannot be null");
        if (status.isBlank()) {
            throw new IllegalArgumentException("Payment status cannot be blank.");
        }
        Objects.requireNonNull(timestamp, "Timestamp cannot be null");
    }
} 