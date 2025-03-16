package com.kasper.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @EntityGraph(attributePaths = {"roles"})
    Optional<User> findByUsername(String username);
    
    @Override
    Optional<User> findById(Long id);
    
    @Override
    List<User> findAll();
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
} 