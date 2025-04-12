package com.kasper.service;

import com.kasper.model.BasicUser;
import com.kasper.repository.BasicUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BasicUserService {

    private final BasicUserRepository userRepository;

    @Autowired
    public BasicUserService(BasicUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<BasicUser> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<BasicUser> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<BasicUser> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<BasicUser> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public BasicUser createUser(BasicUser user) {
        return userRepository.save(user);
    }

    public Optional<BasicUser> updateUser(Long id, BasicUser userDetails) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    existingUser.setUsername(userDetails.getUsername());
                    existingUser.setEmail(userDetails.getEmail());
                    existingUser.setFirstName(userDetails.getFirstName());
                    existingUser.setLastName(userDetails.getLastName());
                    return userRepository.save(existingUser);
                });
    }

    public boolean deleteUser(Long id) {
        return userRepository.findById(id)
                .map(user -> {
                    userRepository.delete(user);
                    return true;
                })
                .orElse(false);
    }
}
