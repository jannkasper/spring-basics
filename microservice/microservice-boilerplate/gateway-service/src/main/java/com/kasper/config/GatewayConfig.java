package com.kasper.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

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
                
                // Other services with JWT authentication handled by global filter
                .route("product-service", r -> r.path("/products/**")
                        .uri("lb://product-service"))
                .route("order-service", r -> r.path("/orders/**")
                        .uri("lb://order-service"))
                .route("payment-service", r -> r.path("/payments/**")
                        .uri("lb://payment-service"))
                .build();
    }
} 