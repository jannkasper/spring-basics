package com.kasper.controller;

import com.kasper.util.JwtDebugUtil;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @GetMapping("/info")
    public String getAdminInfo(@AuthenticationPrincipal Jwt jwt) {
        // Log JWT claims for debugging
        JwtDebugUtil.logJwtClaims(jwt);
        
        String name = jwt.getClaimAsString("name");
        return "Hello, admin! Your name is: " + (name != null ? name : "not available");
    }
    
    @GetMapping("/details")
    @PreAuthorize("hasRole('ADMIN')")
    public String getAdminDetails() {
        return "This is an admin protected endpoint using method security";
    }
    
    @GetMapping("/claims")
    public Map<String, Object> getClaims(@AuthenticationPrincipal Jwt jwt) {
        JwtDebugUtil.logJwtClaims(jwt);
        return jwt.getClaims();
    }
    
    @GetMapping("/roles")
    public String getRoles(@AuthenticationPrincipal org.springframework.security.core.Authentication authentication) {
        return "Your roles: " + authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(", "));
    }
} 