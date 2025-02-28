package com.kasper.user;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface UserDetailsRepository {
    UserDetails save(UserDetails userDetails);
    Optional<UserDetails> findByUsername(String username);
    boolean existsByUsername(String username);
    void deleteByUsername(String username);
}