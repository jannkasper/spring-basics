package com.kasper.role;

public record RoleDto(
    Long id,
    String name
) {
    // Compact canonical constructor for validation
    public RoleDto {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Role name cannot be null or blank");
        }
    }
} 