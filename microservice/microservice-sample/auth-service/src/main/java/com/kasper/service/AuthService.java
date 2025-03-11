package com.kasper.service;

import com.kasper.model.Role;
import com.kasper.model.User;
import com.kasper.payload.request.LoginRequest;
import com.kasper.payload.request.SignupRequest;
import com.kasper.payload.response.JwtResponse;
import com.kasper.payload.response.MessageResponse;
import com.kasper.repository.RoleRepository;
import com.kasper.repository.UserRepository;
import com.kasper.security.jwt.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;

    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        User userDetails = (User) authentication.getPrincipal();
        
        String jwt = jwtUtils.generateToken(userDetails);
        String refreshToken = jwtUtils.generateRefreshToken(userDetails);
        
        String userRole = userDetails.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.joining(", "));

        return JwtResponse.builder()
                .token(jwt)
                .refreshToken(refreshToken)
                .id(userDetails.getId())
                .username(userDetails.getUsername())
                .email(userDetails.getEmail())
                .role(userRole)
                .build();
    }

    public MessageResponse registerUser(SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return new MessageResponse("Error: Username is already taken!");
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return new MessageResponse("Error: Email is already in use!");
        }

        // Create new user's account
        Set<Role> roles = new HashSet<>();
        
        // Default role is USER
        Role userRole = roleRepository.findById(Role.USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roles.add(userRole);
        
        // Add ADMIN role if specified
        if (signUpRequest.getRole() != null && "admin".equals(signUpRequest.getRole())) {
            Role adminRole = roleRepository.findById(Role.ADMIN)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(adminRole);
        }

        User user = User.builder()
                .username(signUpRequest.getUsername())
                .email(signUpRequest.getEmail())
                .password(encoder.encode(signUpRequest.getPassword()))
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .roles(roles)
                .build();

        userRepository.save(user);

        return new MessageResponse("User registered successfully!");
    }
    
    public JwtResponse refreshToken(String refreshToken) {
        if (!refreshToken.startsWith("Bearer ")) {
            throw new RuntimeException("Refresh token is not valid");
        }
        
        String token = refreshToken.substring(7);
        String username = jwtUtils.extractUsername(token);
        
        if (username == null) {
            throw new RuntimeException("Error: Username is null in refresh token");
        }
        
        User userDetails = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Error: User not found"));
        
        if (!jwtUtils.isTokenValid(token, userDetails)) {
            throw new RuntimeException("Error: Refresh token is expired or invalid");
        }
        
        String newAccessToken = jwtUtils.generateToken(userDetails);
        
        String userRole = userDetails.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.joining(", "));
        
        return JwtResponse.builder()
                .token(newAccessToken)
                .refreshToken(token)
                .id(userDetails.getId())
                .username(userDetails.getUsername())
                .email(userDetails.getEmail())
                .role(userRole)
                .build();
    }
} 