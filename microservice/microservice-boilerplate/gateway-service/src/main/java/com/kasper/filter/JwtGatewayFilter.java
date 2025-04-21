package com.kasper.filter;

import com.kasper.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtGatewayFilter extends AbstractGatewayFilterFactory<JwtGatewayFilter.Config> {

    @Autowired
    private JwtUtil jwtUtil;

    public JwtGatewayFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            
            // Skip authentication for auth-service endpoints
            if (request.getURI().getPath().startsWith("/auth-service") ||
                request.getURI().getPath().startsWith("/api/auth") ||
                request.getURI().getPath().equals("/login") ||
                request.getURI().getPath().equals("/register")) {
                return chain.filter(exchange);
            }

            // Handle dashboard and other protected paths
            MultiValueMap<String, HttpCookie> cookies = request.getCookies();
            HttpCookie tokenCookie = cookies != null ? cookies.getFirst(jwtUtil.getCookieName()) : null;

            // If no cookie or invalid token, redirect to login
            if (tokenCookie == null) {
                return redirectToLogin(exchange);
            }

            String token = tokenCookie.getValue();
            
            try {
                // Validate token
                if (!jwtUtil.validateToken(token)) {
                    return redirectToLogin(exchange);
                }

                // Add username as a header
                String username = jwtUtil.extractUsername(token);
                ServerHttpRequest modifiedRequest = request.mutate()
                        .header("X-Auth-Username", username)
                        .build();

                return chain.filter(exchange.mutate().request(modifiedRequest).build());
            } catch (Exception e) {
                // In case of any JWT parsing error
                return redirectToLogin(exchange);
            }
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        return response.setComplete();
    }
    
    private Mono<Void> redirectToLogin(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.FOUND);
        response.getHeaders().add("Location", "/login");
        return response.setComplete();
    }

    public static class Config {
        // Configuration properties if needed
    }
} 