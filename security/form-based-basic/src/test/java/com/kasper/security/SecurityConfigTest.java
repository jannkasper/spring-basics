package com.kasper.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testPublicEndpointsAccessible() throws Exception {
        // Home page should be accessible without authentication
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());

        // Login page should be accessible without authentication
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk());

        // Register page should be accessible without authentication
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk());
    }

    @Test
    public void testSecuredEndpointsRedirectToLogin() throws Exception {
        // Dashboard should redirect to login for unauthenticated users
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        // Admin page should redirect to login for unauthenticated users
        mockMvc.perform(get("/admin"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testUserCanAccessDashboard() throws Exception {
        // User with ROLE_USER should be able to access dashboard
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testUserCannotAccessAdminPage() throws Exception {
        // User with ROLE_USER should not be able to access admin page
        mockMvc.perform(get("/admin"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testAdminCanAccessAdminPage() throws Exception {
        // User with ROLE_ADMIN should be able to access admin page
        mockMvc.perform(get("/admin"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testAdminCanAccessDashboard() throws Exception {
        // User with ROLE_ADMIN should be able to access dashboard
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk());
    }
} 