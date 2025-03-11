package com.kasper.service;

import com.kasper.model.Role;
import com.kasper.repository.RoleRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataInitializationService {

    private final RoleRepository roleRepository;
    
    // This method will be called after the service is initialized
    @PostConstruct
    public void init() {
        initRoles();
    }
    
    private void initRoles() {
        log.info("Checking if roles exist in the database...");
        
        if (roleRepository.findById(Role.USER).isEmpty()) {
            log.info("Creating USER role...");
            Role userRole = new Role();
            userRole.setName(Role.USER);
            userRole.setRoleName("Regular User");
            userRole.setCreatedAt(LocalDateTime.now());
            roleRepository.save(userRole);
        }
        
        if (roleRepository.findById(Role.ADMIN).isEmpty()) {
            log.info("Creating ADMIN role...");
            Role adminRole = new Role();
            adminRole.setName(Role.ADMIN);
            adminRole.setRoleName("Administrator");
            adminRole.setCreatedAt(LocalDateTime.now());
            roleRepository.save(adminRole);
        }
        
        log.info("Roles initialization completed.");
    }
} 