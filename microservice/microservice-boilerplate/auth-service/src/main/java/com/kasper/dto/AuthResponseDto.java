package com.kasper.dto;

import java.util.Objects;
import java.util.Set;

public record AuthResponseDto(
    String username,
    Set<String> roles,
    String token
) {
    public AuthResponseDto {
        Objects.requireNonNull(username, "Username cannot be null");
        Objects.requireNonNull(roles, "Roles cannot be null");
        Objects.requireNonNull(token, "Token cannot be null");
    }
} 