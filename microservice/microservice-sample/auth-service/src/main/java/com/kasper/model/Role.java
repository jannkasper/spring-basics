package com.kasper.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "roles")
public class Role {
    
    @Id
    @Column(length = 50)
    private String name;
    
    @Column(length = 100)
    private String roleName;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    public static final String USER = "ROLE_USER";
    public static final String ADMIN = "ROLE_ADMIN";
} 