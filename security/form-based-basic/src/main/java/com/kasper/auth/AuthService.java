package com.kasper.auth;

import com.kasper.user.UserDto;

public interface AuthService {
    
    UserDto register(UserDto registrationDto);
    
    boolean authenticate(String username, String password);
    
    UserDto getCurrentUser();
} 