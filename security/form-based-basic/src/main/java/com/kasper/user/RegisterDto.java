package com.kasper.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for registration form without validation.
 * This is used to initialize the form without throwing exceptions.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDto {
    private String username;
    private String email;
    private String password;
    private String confirmPassword;
} 