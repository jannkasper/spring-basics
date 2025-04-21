package com.kasper.config;

import com.kasper.filter.JwtAuthenticationFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public GatewayConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Auth service routes - public endpoints
                .route("auth-service-login", r -> r.path("/login/**")
                        .uri("lb://auth-service"))
                .route("auth-service-register", r -> r.path("/register/**")
                        .uri("lb://auth-service"))
                .route("auth-service-dashboard", r -> r.path("/dashboard/**")
                        .uri("lb://auth-service"))
                .route("auth-service-api", r -> r.path("/api/auth/**")
                        .uri("lb://auth-service"))
                
                // Other services with JWT authentication
                .route("product-service", r -> r.path("/products/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())))
                        .uri("lb://product-service"))
                .route("order-service", r -> r.path("/orders/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())))
                        .uri("lb://order-service"))
                .route("payment-service", r -> r.path("/payments/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())))
                        .uri("lb://payment-service"))
                .build();
    }
} 