package com.kasper.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kasper.role.RoleDto;
import com.kasper.user.UserDto;
import com.kasper.auth.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Test
    public void testRegisterSuccess() throws Exception {
        // Prepare test data
        Set<RoleDto> roles = new HashSet<>();
        roles.add(new RoleDto(1L, "ROLE_USER"));
        
        UserDto inputUser = new UserDto(null, "testuser", "test@example.com", "password123", true, null);
        UserDto registeredUser = new UserDto(1L, "testuser", "test@example.com", null, true, roles);
        
        // Mock service behavior
        when(authService.register(any(UserDto.class))).thenReturn(registeredUser);
        
        // Perform request and validate response
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.username").value("testuser"));
    }

    @Test
    public void testLoginSuccess() throws Exception {
        // Prepare test data
        Set<RoleDto> roles = new HashSet<>();
        roles.add(new RoleDto(1L, "ROLE_USER"));
        UserDto loggedInUser = new UserDto(1L, "testuser", "test@example.com", null, true, roles);
        
        // Mock service behavior
        when(authService.authenticate(anyString(), anyString())).thenReturn(true);
        when(authService.getCurrentUser()).thenReturn(loggedInUser);
        
        // Perform request and validate response
        mockMvc.perform(post("/api/auth/login")
                .param("username", "testuser")
                .param("password", "password123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data.username").value("testuser"));
    }

    @Test
    public void testLoginFailure() throws Exception {
        // Mock service behavior
        when(authService.authenticate(anyString(), anyString())).thenReturn(false);
        
        // Perform request and validate response
        mockMvc.perform(post("/api/auth/login")
                .param("username", "testuser")
                .param("password", "wrongpassword"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.result").value("ERROR"));
    }

    @Test
    public void testGetCurrentUserAuthenticated() throws Exception {
        // Prepare test data
        Set<RoleDto> roles = new HashSet<>();
        roles.add(new RoleDto(1L, "ROLE_USER"));
        UserDto currentUser = new UserDto(1L, "testuser", "test@example.com", null, true, roles);
        
        // Mock service behavior
        when(authService.getCurrentUser()).thenReturn(currentUser);
        
        // Perform request and validate response
        mockMvc.perform(get("/api/auth/current-user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data.username").value("testuser"));
    }

    @Test
    public void testGetCurrentUserNotAuthenticated() throws Exception {
        // Mock service behavior
        when(authService.getCurrentUser()).thenReturn(null);
        
        // Perform request and validate response
        mockMvc.perform(get("/api/auth/current-user"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.result").value("ERROR"));
    }
} 