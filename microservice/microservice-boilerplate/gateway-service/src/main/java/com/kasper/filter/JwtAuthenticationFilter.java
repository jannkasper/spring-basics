package com.kasper.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kasper.security.JwtUtil;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;
    private static final String JWT_COOKIE_NAME = "jwt_token";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        
        // Skip authentication for public endpoints
        if (isPublicEndpoint(request.getPath().toString())) {
            return chain.filter(exchange);
        }

        // Try to get token from cookies first
        String token = getTokenFromCookies(exchange);
        
        // If no token in cookies, try from Authorization header
        if (token == null) {
            token = getTokenFromHeader(exchange);
        }
        
        // If still no token, return error response
        if (token == null) {
            return onError(exchange, "Authentication required", HttpStatus.UNAUTHORIZED);
        }

        // Validate the token
        if (!jwtUtil.validateToken(token)) {
            return onError(exchange, "Invalid or expired token", HttpStatus.UNAUTHORIZED);
        }

        // Add user info to request headers if needed
        ServerHttpRequest modifiedRequest = request.mutate()
                .header("X-Auth-User", jwtUtil.extractUsername(token))
                .build();

        return chain.filter(exchange.mutate().request(modifiedRequest).build());
    }
    
    private String getTokenFromCookies(ServerWebExchange exchange) {
        List<HttpCookie> cookies = exchange.getRequest().getCookies().get(JWT_COOKIE_NAME);
        if (cookies != null && !cookies.isEmpty()) {
            return cookies.get(0).getValue();
        }
        return null;
    }
    
    private String getTokenFromHeader(ServerWebExchange exchange) {
        List<String> authHeaders = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION);
        if (authHeaders != null && !authHeaders.isEmpty()) {
            String authHeader = authHeaders.get(0);
            if (authHeader.startsWith("Bearer ")) {
                return authHeader.substring(7);
            }
        }
        return null;
    }

    private boolean isPublicEndpoint(String path) {
        return path.contains("/login") || 
               path.contains("/register") || 
               path.contains("/dashboard") ||
               path.contains("/api/auth");
    }

    private Mono<Void> onError(ServerWebExchange exchange, String errorMessage, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("result", "ERROR");
        errorDetails.put("message", errorMessage);
        errorDetails.put("data", null);
        
        try {
            byte[] bytes = objectMapper.writeValueAsBytes(errorDetails);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            return response.setComplete();
        }
    }

    @Override
    public int getOrder() {
        return -100; // High precedence
    }
} 