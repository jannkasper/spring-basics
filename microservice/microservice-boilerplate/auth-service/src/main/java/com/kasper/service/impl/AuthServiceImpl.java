package com.kasper.service.impl;

import com.kasper.dto.AuthResponseDto;
import com.kasper.dto.LoginRequestDto;
import com.kasper.dto.RegisterRequestDto;
import com.kasper.model.User;
import com.kasper.repository.UserRepository;
import com.kasper.security.JwtUtil;
import com.kasper.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public AuthResponseDto login(LoginRequestDto loginRequest, HttpServletResponse response) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        
        String token = jwtUtil.generateToken(userDetails);
        addTokenCookie(response, token);
        
        Set<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(role -> role.replace("ROLE_", ""))
                .collect(Collectors.toSet());
                
        return new AuthResponseDto(userDetails.getUsername(), roles, token);
    }

    @Override
    @Transactional
    public AuthResponseDto register(RegisterRequestDto registerRequest, HttpServletResponse response) {
        if (userRepository.existsByUsername(registerRequest.username())) {
            throw new IllegalArgumentException("Username is already taken");
        }

        if (userRepository.existsByEmail(registerRequest.email())) {
            throw new IllegalArgumentException("Email is already in use");
        }

        User user = new User();
        user.setUsername(registerRequest.username());
        user.setEmail(registerRequest.email());
        user.setPassword(passwordEncoder.encode(registerRequest.password()));
        
        Set<String> roles = new HashSet<>();
        roles.add("USER");
        user.setRoles(roles);

        userRepository.save(user);

        LoginRequestDto loginRequest = new LoginRequestDto(
                registerRequest.username(),
                registerRequest.password()
        );
        
        return login(loginRequest, response);
    }

    @Override
    public void logout(HttpServletResponse response) {
        // Clear the security context to invalidate the current authentication
        SecurityContextHolder.clearContext();
        
        // Delete the JWT cookie
        Cookie jwtCookie = new Cookie(jwtUtil.getCookieName(), null);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0);
        response.addCookie(jwtCookie);
        
        // Use multiple variations of JSESSIONID cookie removal for different browser behaviors
        String[] cookiePaths = {"/", "/api", "/api/auth", ""};
        
        for (String path : cookiePaths) {
            Cookie sessionCookie = new Cookie("JSESSIONID", null);
            sessionCookie.setHttpOnly(true);
            sessionCookie.setSecure(true);
            sessionCookie.setPath(path.isEmpty() ? "/" : path);
            sessionCookie.setMaxAge(0);
            response.addCookie(sessionCookie);
        }
        
        // Set additional response headers to help with cookie clearing
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
        
        // Add a Set-Cookie header with explicit directives for JSESSIONID removal
        // This approach works for some browsers where regular Cookie API might not
        response.addHeader("Set-Cookie", "JSESSIONID=; Path=/; Expires=Thu, 01 Jan 1970 00:00:00 GMT; Max-Age=0; HttpOnly; Secure; SameSite=Strict");
    }
    
    private void addTokenCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie(jwtUtil.getCookieName(), token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setDomain("localhost");
        cookie.setMaxAge((int) (jwtUtil.extractExpiration(token).getTime() - System.currentTimeMillis()) / 1000);
        response.addCookie(cookie);
    }
} 