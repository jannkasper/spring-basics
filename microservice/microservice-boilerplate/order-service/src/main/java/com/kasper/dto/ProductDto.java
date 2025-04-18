package com.kasper.dto;

import java.math.BigDecimal;

// DTO for receiving product data from product-service
// Using record as per instructions, no validation needed here as it's response data
public record ProductDto(
        Long id,
        String name,
        BigDecimal price
) {} 