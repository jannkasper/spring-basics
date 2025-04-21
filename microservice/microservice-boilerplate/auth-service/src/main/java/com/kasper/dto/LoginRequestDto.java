package com.kasper.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.Objects;

public record LoginRequestDto(
    @NotEmpty
    String username,
    
    @NotEmpty
    String password
) {
    public LoginRequestDto {
        Objects.requireNonNull(username, "Username cannot be null");
        Objects.requireNonNull(password, "Password cannot be null");
    }
} 