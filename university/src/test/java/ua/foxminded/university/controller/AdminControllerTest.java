package ua.foxminded.university.controller;

import static org.mockito.Mockito.when;
import java.util.Optional;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ua.foxminded.university.dao.entities.Admin;
import ua.foxminded.university.dao.service.AdminService;
import ua.foxminded.university.validation.ControllerBindingValidator;

@WebMvcTest({ AdminController.class, ControllerBindingValidator.class })
@ActiveProfiles("test-container")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class AdminControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private AdminService adminService;

	@Test
	@WithMockUser("ADMIN")
	void testAdminMainPage() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/admin/main")).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("admin/main"));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void testGetAllAdmin() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/admin/admin-list"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.model().attributeExists("admins"))
				.andExpect(MockMvcResultMatchers.view().name("admin/admin-list"));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void testDeleteAdmin() throws Exception {
		int adminId = 1;
		mockMvc.perform(MockMvcRequestBuilders.post("/admin/delete/{adminId}", adminId))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"))
				.andExpect(MockMvcResultMatchers.redirectedUrl("/admin/admin-list"));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void testOpenAdminCard_WhenAdminExists() throws Exception {
		Admin admin = new Admin();
		admin.setId(1);

		when(adminService.findAdminById(admin.getId())).thenReturn(Optional.of(admin));

		mockMvc.perform(MockMvcRequestBuilders.get("/admin/admin-card/{adminId}", admin.getId()))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("admin/admin-card"))
				.andExpect(MockMvcResultMatchers.model().attributeExists("admin"))
				.andExpect(MockMvcResultMatchers.model().attribute("admin", Matchers.sameInstance(admin)));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void testOpenAdminCard_WhenAdminDoesNotExist() throws Exception {
		int adminId = 999;

		when(adminService.findAdminById(adminId)).thenReturn(Optional.empty());

		mockMvc.perform(MockMvcRequestBuilders.get("/admin/admin-card/{adminId}", adminId))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.redirectedUrl("/admin/admin-list"));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void testShowCreateAdminForm() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/admin/create-admin"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("admin/create-admin"));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void testCreateAdmin() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/admin/create-admin"))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.flash().attributeCount(1))
				.andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
	}

}
