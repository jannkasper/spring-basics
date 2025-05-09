package com.kasper.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // Allow requests from the frontend domain
        config.addAllowedOrigin("http://localhost:5173"); // Vite dev server default port
        config.addAllowedOrigin("http://localhost:5174"); // In case the default port is in use
        config.addAllowedOrigin("http://localhost:5175"); // In case the default port is in use
        config.addAllowedOrigin("http://localhost:5176"); // In case the default port is in use
        config.addAllowedOrigin("http://localhost:5177"); // In case the default port is in use
        
        // Allow common HTTP methods
        config.addAllowedMethod("GET");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("DELETE");
        config.addAllowedMethod("OPTIONS");
        
        // Allow all headers
        config.addAllowedHeader("*");
        
        // Allow credentials (cookies, authorization headers, etc.)
        config.setAllowCredentials(true);
        
        // Apply this configuration to all paths
        source.registerCorsConfiguration("/api/**", config);
        
        return new CorsFilter(source);
    }
} 