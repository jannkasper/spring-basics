package com.kasper.user;

import java.util.List;

public interface UserService {
    
    UserDto createUser(UserDto userDto);
    
    UserDto getUserById(Long id);
    
    UserDto getUserByUsername(String username);
    
    List<UserDto> getAllUsers();
    
    UserDto updateUser(Long id, UserDto userDto);
    
    void deleteUser(Long id);
} 