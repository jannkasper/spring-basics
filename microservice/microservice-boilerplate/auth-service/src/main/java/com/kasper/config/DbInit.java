package com.kasper.config;

import com.kasper.model.User;
import com.kasper.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class DbInit implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Create default admin if not exists
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@example.com");
            
            Set<String> roles = new HashSet<>();
            roles.add("ADMIN");
            roles.add("USER");
            admin.setRoles(roles);
            
            userRepository.save(admin);
            System.out.println("Default admin user created.");
        }
        
        // Create another admin account
        if (!userRepository.existsByUsername("superadmin")) {
            User superAdmin = new User();
            superAdmin.setUsername("superadmin");
            superAdmin.setPassword(passwordEncoder.encode("superadmin123"));
            superAdmin.setEmail("superadmin@example.com");
            
            Set<String> roles = new HashSet<>();
            roles.add("ADMIN");
            roles.add("USER");
            superAdmin.setRoles(roles);
            
            userRepository.save(superAdmin);
            System.out.println("Super admin user created.");
        }
    }
} 