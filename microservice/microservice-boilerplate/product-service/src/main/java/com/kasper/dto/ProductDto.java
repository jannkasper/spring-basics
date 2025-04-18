package com.kasper.dto;

import java.math.BigDecimal;
import java.util.Objects;

// Using record for DTO as per instructions
public record ProductDto(
        Long id,
        String name,
        BigDecimal price
) {
    // Compact canonical constructor for validation
    public ProductDto {
        Objects.requireNonNull(id, "Product ID cannot be null");
        if (id <= 0) {
            throw new IllegalArgumentException("Product ID must be positive.");
        }
        Objects.requireNonNull(name, "Product name cannot be null");
        if (name.isBlank()) {
            throw new IllegalArgumentException("Product name cannot be blank.");
        }
        Objects.requireNonNull(price, "Product price cannot be null");
        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Product price cannot be negative.");
        }
    }
} 