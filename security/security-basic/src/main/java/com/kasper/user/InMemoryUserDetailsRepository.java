package com.kasper.user;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Repository
public class InMemoryUserDetailsRepository implements UserDetailsRepository {
    private final Map<String, UserDetails> users = new HashMap<>();
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostConstruct
    public void initialize() {
        // Initialize with some sample users
        CustomUserDetails user1 = new CustomUserDetails(
            "user",
            passwordEncoder.encode("password"),
            Set.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        CustomUserDetails admin = new CustomUserDetails(
            "admin",
            passwordEncoder.encode("admin"),
            Set.of(
                new SimpleGrantedAuthority("ROLE_USER"),
                new SimpleGrantedAuthority("ROLE_ADMIN")
            )
        );

        save(user1);
        save(admin);
    }

    @Override
    public UserDetails save(UserDetails userDetails) {
        users.put(userDetails.getUsername(), userDetails);
        return userDetails;
    }

    @Override
    public Optional<UserDetails> findByUsername(String username) {
        return Optional.ofNullable(users.get(username));
    }

    @Override
    public boolean existsByUsername(String username) {
        return users.containsKey(username);
    }

    @Override
    public void deleteByUsername(String username) {
        users.remove(username);
    }
}