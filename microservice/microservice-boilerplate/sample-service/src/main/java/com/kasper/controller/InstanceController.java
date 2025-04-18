package com.kasper.controller;

import com.kasper.dto.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/instance")
public class InstanceController {

    @Value("${spring.application.name}")
    private String applicationName;
    
    @Value("${instance.name:default-instance}")
    private String instanceName;

    @GetMapping("/info")
    public ResponseEntity<ApiResponse<String>> getInstanceInfo() {
        try {
            String message = String.format("Service: %s, Instance: %s", applicationName, instanceName);
            ApiResponse<String> response = new ApiResponse<>("SUCCESS", message, instanceName);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>("ERROR", e.getMessage(), null)
            );
        }
    }
} 