package com.kasper.controller;

import com.kasper.dto.ApiResponse;
import com.kasper.dto.AuthResponseDto;
import com.kasper.dto.LoginRequestDto;
import com.kasper.dto.RegisterRequestDto;
import com.kasper.exception.GlobalExceptionHandler;
import com.kasper.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(
            @Valid @RequestBody LoginRequestDto loginRequest,
            HttpServletResponse response) {
        try {
            AuthResponseDto authResponse = authService.login(loginRequest, response);
            return ResponseEntity.ok(
                    ApiResponse.success("Login successful", authResponse)
            );
        } catch (Exception e) {
            return GlobalExceptionHandler.errorResponseEntity(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> register(
            @Valid @RequestBody RegisterRequestDto registerRequest,
            HttpServletResponse response) {
        try {
            AuthResponseDto authResponse = authService.register(registerRequest, response);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    ApiResponse.success("User registered successfully", authResponse)
            );
        } catch (Exception e) {
            return GlobalExceptionHandler.errorResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<?>> logout(HttpServletResponse response) {
        try {
            authService.logout(response);
            return ResponseEntity.ok(
                    ApiResponse.success("Logged out successfully", null)
            );
        } catch (Exception e) {
            return GlobalExceptionHandler.errorResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
} 