package com.kasper.user;

import com.kasper.role.RoleDto;
import com.kasper.role.Role;
import com.kasper.role.RoleRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        // Check if username or email already exists
        if (userRepository.existsByUsername(userDto.username())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(userDto.email())) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Create user entity from DTO
        User user = User.builder()
                .username(userDto.username())
                .email(userDto.email())
                .password(passwordEncoder.encode(userDto.password()))
                .enabled(userDto.enabled())
                .build();

        // Handle roles
        Set<Role> roles = new HashSet<>();
        if (userDto.roles() != null && !userDto.roles().isEmpty()) {
            roles = userDto.roles().stream()
                    .map(roleDto -> roleRepository.findByName(roleDto.name())
                            .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleDto.name())))
                    .collect(Collectors.toSet());
        } else {
            // Default role (USER) if no roles provided
            Role userRole = roleRepository.findByName("ROLE_USER")
                    .orElseThrow(() -> new IllegalArgumentException("Default role 'ROLE_USER' not found"));
            roles.add(userRole);
        }
        user.setRoles(roles);

        // Save user
        User savedUser = userRepository.save(user);
        
        // Convert saved user to DTO and return
        return convertToDto(savedUser);
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
        return convertToDto(user);
    }

    @Override
    public UserDto getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found with username: " + username));
        return convertToDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDto updateUser(Long id, UserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));

        // Check if new username already exists for another user
        if (!user.getUsername().equals(userDto.username()) && 
                userRepository.existsByUsername(userDto.username())) {
            throw new IllegalArgumentException("Username already exists");
        }

        // Check if new email already exists for another user
        if (!user.getEmail().equals(userDto.email()) && 
                userRepository.existsByEmail(userDto.email())) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Update basic fields
        user.setUsername(userDto.username());
        user.setEmail(userDto.email());
        user.setEnabled(userDto.enabled());
        
        // Only update password if provided
        if (userDto.password() != null && !userDto.password().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDto.password()));
        }

        // Update roles if provided
        if (userDto.roles() != null && !userDto.roles().isEmpty()) {
            Set<Role> roles = userDto.roles().stream()
                    .map(roleDto -> roleRepository.findByName(roleDto.name())
                            .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleDto.name())))
                    .collect(Collectors.toSet());
            user.setRoles(roles);
        }

        // Save updated user
        User updatedUser = userRepository.save(user);
        
        // Convert to DTO and return
        return convertToDto(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    // Helper method to convert User entity to UserDto
    private UserDto convertToDto(User user) {
        Set<RoleDto> roleDtos = user.getRoles().stream()
                .map(role -> new RoleDto(role.getId(), role.getName()))
                .collect(Collectors.toSet());

        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                null, // Don't include password in DTO
                user.isEnabled(),
                roleDtos
        );
    }
} 