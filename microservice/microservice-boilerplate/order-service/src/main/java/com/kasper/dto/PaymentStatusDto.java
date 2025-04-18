package com.kasper.dto;

import java.time.LocalDateTime;

// DTO for receiving payment status from payment-service
// Using record as per instructions, no validation needed here as it's response data
public record PaymentStatusDto(
        String orderId,
        String status,
        LocalDateTime timestamp
) {} 