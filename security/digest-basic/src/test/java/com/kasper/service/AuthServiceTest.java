package com.kasper.service;

import com.kasper.user.UserDto;
import com.kasper.auth.AuthServiceImpl;
import com.kasper.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class AuthServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private AuthServiceImpl authService;

    private UserDto testUserDto;

    @BeforeEach
    public void setup() {
        testUserDto = new UserDto(1L, "testuser", "test@example.com", "password", true, new HashSet<>());
        
        // Setup SecurityContextHolder mock
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    public void testRegister() {
        // Mock behavior
        when(userService.createUser(any(UserDto.class))).thenReturn(testUserDto);
        
        // Test
        UserDto result = authService.register(testUserDto);
        
        // Verify
        assertNotNull(result);
        assertEquals("testuser", result.username());
        verify(userService, times(1)).createUser(any(UserDto.class));
    }

    @Test
    public void testAuthenticateSuccess() {
        // Mock behavior
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        
        // Test
        boolean result = authService.authenticate("testuser", "password");
        
        // Verify
        assertTrue(result);
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(securityContext, times(1)).setAuthentication(authentication);
    }

    @Test
    public void testAuthenticateFailure() {
        // Mock behavior
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Authentication failed"));
        
        // Test
        boolean result = authService.authenticate("testuser", "wrongpassword");
        
        // Verify
        assertFalse(result);
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(securityContext, never()).setAuthentication(any(Authentication.class));
    }

    @Test
    public void testGetCurrentUserAuthenticated() {
        // Mock behavior
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("testuser");
        when(userService.getUserByUsername("testuser")).thenReturn(testUserDto);
        
        // Test
        UserDto result = authService.getCurrentUser();
        
        // Verify
        assertNotNull(result);
        assertEquals("testuser", result.username());
        verify(userService, times(1)).getUserByUsername("testuser");
    }

    @Test
    public void testGetCurrentUserNotAuthenticated() {
        // Mock behavior
        when(authentication.isAuthenticated()).thenReturn(false);
        
        // Test
        UserDto result = authService.getCurrentUser();
        
        // Verify
        assertNull(result);
        verify(userService, never()).getUserByUsername(anyString());
    }

    @Test
    public void testGetCurrentUserAnonymous() {
        // Mock behavior
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("anonymousUser");
        
        // Test
        UserDto result = authService.getCurrentUser();
        
        // Verify
        assertNull(result);
        verify(userService, never()).getUserByUsername(anyString());
    }
} 