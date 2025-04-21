package com.kasper.service;

import com.kasper.dto.AuthResponseDto;
import com.kasper.dto.LoginRequestDto;
import com.kasper.dto.RegisterRequestDto;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    AuthResponseDto login(LoginRequestDto loginRequest, HttpServletResponse response);
    AuthResponseDto register(RegisterRequestDto registerRequest, HttpServletResponse response);
    void logout(HttpServletResponse response);
} 