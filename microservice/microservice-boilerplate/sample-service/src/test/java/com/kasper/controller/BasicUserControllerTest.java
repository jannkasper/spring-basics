package com.kasper.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kasper.model.BasicUser;
import com.kasper.service.BasicUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BasicUserController.class)
public class BasicUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BasicUserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetAllUsers() throws Exception {
        BasicUser user1 = new BasicUser(1L, "johndoe", "john.doe@example.com", "John", "Doe");
        BasicUser user2 = new BasicUser(2L, "janedoe", "jane.doe@example.com", "Jane", "Doe");
        
        when(userService.getAllUsers()).thenReturn(Arrays.asList(user1, user2));
        
        mockMvc.perform(get("/api/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].username").value("johndoe"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].username").value("janedoe"));
    }

    @Test
    public void testGetUserById() throws Exception {
        BasicUser user = new BasicUser(1L, "johndoe", "john.doe@example.com", "John", "Doe");
        
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));
        
        mockMvc.perform(get("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("johndoe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    public void testCreateUser() throws Exception {
        BasicUser userToCreate = new BasicUser(null, "newuser", "new.user@example.com", "New", "User");
        BasicUser createdUser = new BasicUser(1L, "newuser", "new.user@example.com", "New", "User");
        
        when(userService.createUser(any(BasicUser.class))).thenReturn(createdUser);
        
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userToCreate)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("newuser"))
                .andExpect(jsonPath("$.email").value("new.user@example.com"));
    }

    @Test
    public void testUpdateUser() throws Exception {
        BasicUser userToUpdate = new BasicUser(1L, "updateduser", "updated.user@example.com", "Updated", "User");
        
        when(userService.updateUser(eq(1L), any(BasicUser.class))).thenReturn(Optional.of(userToUpdate));
        
        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userToUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("updateduser"))
                .andExpect(jsonPath("$.email").value("updated.user@example.com"));
    }

    @Test
    public void testDeleteUser() throws Exception {
        when(userService.deleteUser(1L)).thenReturn(true);
        
        mockMvc.perform(delete("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}