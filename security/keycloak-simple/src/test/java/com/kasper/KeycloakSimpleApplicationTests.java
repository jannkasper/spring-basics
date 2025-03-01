package com.kasper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class KeycloakSimpleApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void publicEndpointAccessible() throws Exception {
		mockMvc.perform(get("/api/public/hello"))
				.andExpect(status().isOk())
				.andExpect(content().string("Hello, public user!"));
	}

	@Test
	void unauthorizedUserCannotAccessProtectedEndpoints() throws Exception {
		mockMvc.perform(get("/api/user/info"))
				.andExpect(status().isUnauthorized());

		mockMvc.perform(get("/api/admin/info"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	@WithMockUser(roles = "USER")
	void userCanAccessUserEndpoint() throws Exception {
		mockMvc.perform(get("/api/user/info"))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(roles = "USER")
	void userCannotAccessAdminEndpoint() throws Exception {
		mockMvc.perform(get("/api/admin/info"))
				.andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void adminCanAccessAdminEndpoint() throws Exception {
		mockMvc.perform(get("/api/admin/info"))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(roles = {"ADMIN", "USER"})
	void adminCanAccessUserEndpoint() throws Exception {
		mockMvc.perform(get("/api/user/info"))
				.andExpect(status().isOk());
	}
}
