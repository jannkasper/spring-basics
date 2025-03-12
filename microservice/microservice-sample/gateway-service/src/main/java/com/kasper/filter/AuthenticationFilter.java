package com.kasper.filter;

import com.kasper.security.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Slf4j
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final JwtUtils jwtUtils;
    
    private final List<String> excludedUrls = List.of(
        "/api/sample/health"
    );

    public AuthenticationFilter(JwtUtils jwtUtils) {
        super(Config.class);
        this.jwtUtils = jwtUtils;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            
            // Skip authentication for excluded paths
            final String path = request.getPath().toString();
            if (isSecured(path)) {
                if (!hasValidToken(request)) {
                    return onError(exchange, HttpStatus.UNAUTHORIZED);
                }
                
                // Add user information to headers if needed
                String token = jwtUtils.parseJwt(request);
                String username = jwtUtils.extractUsername(token);
                
                // You can add the username to the request headers if needed by the services
                ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                    .header("X-User-Name", username)
                    .build();
                
                return chain.filter(exchange.mutate().request(modifiedRequest).build());
            }
            
            return chain.filter(exchange);
        };
    }

    private boolean isSecured(String path) {
        return excludedUrls.stream().noneMatch(path::equals);
    }

    private boolean hasValidToken(ServerHttpRequest request) {
        String token = jwtUtils.parseJwt(request);
        
        if (token == null) {
            return false;
        }
        
        return jwtUtils.validateJwtToken(token);
    }

    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }

    public static class Config {
        // configuration properties if needed
    }
}