package com.kasper.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;

/**
 * Web configuration to forward non-API requests to the frontend
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String resourcePath, Resource location) throws IOException {
                        Resource resource = location.createRelative(resourcePath);
                        
                        // If the requested resource exists, return it
                        if (resource.exists() && resource.isReadable()) {
                            return resource;
                        }
                        
                        // If path starts with /api, don't return index.html
                        if (resourcePath.startsWith("api/")) {
                            return null;
                        }
                        
                        // For all other paths, return index.html for SPA routing
                        return new ClassPathResource("/static/index.html");
                    }
                });
    }
} 