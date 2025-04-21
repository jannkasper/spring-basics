package com.kasper.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@Component
public class JwtAuthenticationFilter implements WebFilter {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.cookie.name}")
    private String jwtCookieName;
    
    // List of paths that should be excluded from authentication
    private final List<String> excludedPaths = Arrays.asList(
            "/login", 
            "/register",
            "/api/auth/login", 
            "/api/auth/register", 
            "/api/auth/logout"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {


        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // Skip authentication for excluded paths
        if (shouldSkip(path)) {
            return chain.filter(exchange);
        }

        // Check for JWT token in cookies
        HttpCookie cookie = request.getCookies().getFirst(jwtCookieName);
        if (cookie == null) {
            // No JWT token found in cookies
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = cookie.getValue();
        try {
            // Validate JWT token
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // If we get here, the token is valid - you could add user info to the exchange attributes
            // For example:
            exchange.getAttributes().put("userId", claims.getSubject());
            
            // Continue with the filter chain
            return chain.filter(exchange);
        } catch (Exception e) {
            // Invalid JWT token
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    private boolean shouldSkip(String path) {
        return excludedPaths.stream().anyMatch(path::startsWith);
    }
} 