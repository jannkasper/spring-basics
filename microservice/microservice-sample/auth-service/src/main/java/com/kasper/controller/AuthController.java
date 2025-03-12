package com.kasper.controller;

import com.kasper.payload.request.LoginRequest;
import com.kasper.payload.request.SignupRequest;
import com.kasper.payload.request.TokenRefreshRequest;
import com.kasper.payload.response.JwtResponse;
import com.kasper.payload.response.MessageResponse;
import com.kasper.payload.response.TokenRefreshResponse;
import com.kasper.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signin")
    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        JwtResponse response = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/signup")
    public ResponseEntity<MessageResponse> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        MessageResponse response = authService.registerUser(signUpRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<TokenRefreshResponse> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        JwtResponse jwtResponse = authService.refreshToken(request.getRefreshToken());
        
        return ResponseEntity.ok(TokenRefreshResponse.builder()
                .accessToken(jwtResponse.getToken())
                .refreshToken(jwtResponse.getRefreshToken())
                .build());
    }
    
    /**
     * Public endpoint that doesn't require authentication
     */
    @GetMapping("/public")
    public ResponseEntity<MessageResponse> publicEndpoint() {
        return ResponseEntity.ok(new MessageResponse("This is a public endpoint - no authentication required!"));
    }
    
    /**
     * Protected endpoint that requires authentication with ROLE_USER
     */
    @GetMapping("/protected")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<MessageResponse> protectedEndpoint() {
        return ResponseEntity.ok(new MessageResponse("This is a protected endpoint - authentication required!"));
    }
}