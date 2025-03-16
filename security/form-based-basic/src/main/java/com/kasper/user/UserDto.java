package com.kasper.user;

import com.kasper.role.RoleDto;

import java.util.Set;

public record UserDto(
    Long id,
    String username,
    String email,
    String password,
    boolean enabled,
    Set<RoleDto> roles
) {
    // Compact canonical constructor for validation
    public UserDto {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or blank");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank");
        }
    }
} 