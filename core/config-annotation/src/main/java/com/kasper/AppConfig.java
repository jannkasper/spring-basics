package com.kasper;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@ComponentScan(basePackages = "com.kasper")
public class AppConfig {

    @Bean
    public List<Product> products() {
        return Arrays.asList(
                new Product("Laptop", 1200.00),
                new Product("Smartphone", 800.00)
        );
    }

    @Bean
    public Map<String, Customer> customers() {
        Map<String, Customer> customers = new HashMap<>();
        customers.put("C001", new Customer("Alice Johnson", "alice@example.com"));
        customers.put("C002", new Customer("Bob Smith", "bob@example.com"));
        return customers;
    }
}
