package com.kasper.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.Objects;

public record RegisterRequestDto(
    @NotEmpty
    @Size(min = 3, max = 50)
    String username,
    
    @NotEmpty
    @Email
    String email,
    
    @NotEmpty
    @Size(min = 6)
    String password
) {
    public RegisterRequestDto {
        Objects.requireNonNull(username, "Username cannot be null");
        Objects.requireNonNull(email, "Email cannot be null");
        Objects.requireNonNull(password, "Password cannot be null");
    }
} 