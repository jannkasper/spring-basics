package com.kasper.config;

import com.kasper.model.BasicUser;
import com.kasper.repository.BasicUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(@Autowired BasicUserRepository userRepository) {
        return args -> {
            // Create sample users for testing
            BasicUser user1 = new BasicUser(null, "johndoe", "john.doe@example.com", "John", "Doe");
            BasicUser user2 = new BasicUser(null, "janedoe", "jane.doe@example.com", "Jane", "Doe");
            BasicUser user3 = new BasicUser(null, "bobsmith", "bob.smith@example.com", "Bob", "Smith");
            
            userRepository.save(user1);
            userRepository.save(user2);
            userRepository.save(user3);
            
            System.out.println("Sample users initialized.");
        };
    }
}