package ua.foxminded.university.controller;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import java.util.Optional;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@WebMvcTest({ AdminController.class, ControllerBindingValidator.class })
@ActiveProfiles("test-container")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@WithMockUser(roles = "ADMIN")
class AdminControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private AdminService adminService;

	@Test
	void testAdminDashboard_WhenUserAuthenticated() throws Exception {
		Admin admin = new Admin();
		admin.setEmail("admin@example.ua");

		when(adminService.findAdminByEmail(admin.getEmail())).thenReturn(Optional.of(admin));

		Authentication auth = new UsernamePasswordAuthenticationToken(admin.getEmail(), null,
				AuthorityUtils.createAuthorityList("ROLE_ADMIN"));
		SecurityContextHolder.getContext().setAuthentication(auth);

		mockMvc.perform(MockMvcRequestBuilders.get("/admin/main")).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("admin/main"))
				.andExpect(MockMvcResultMatchers.model().attributeExists("admin"))
				.andExpect(MockMvcResultMatchers.model().attribute("admin", admin));
	}

	@Test
	void testAdminDashboard_WhenUserNotAuthenticated() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/admin/main"))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.redirectedUrl("/login"));
	}

	@Test
	void testUpdatePersonalData_Success() throws Exception {
		int adminId = 1;
		Admin updatedAdmin = new Admin();

		when(adminService.updateAdminById(adminId, updatedAdmin)).thenReturn(updatedAdmin);

		mockMvc.perform(MockMvcRequestBuilders.post("/admin/update-personal/{adminId}", adminId).with(csrf().asHeader())
				.flashAttr("admin", updatedAdmin)).andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"))
				.andExpect(MockMvcResultMatchers.redirectedUrl("/admin/main"));
	}

	@Test
	void testUpdatePersonalData_Failure() throws Exception {
		int adminId = 1;
		mockMvc.perform(
				MockMvcRequestBuilders.post("/admin/update-personal/{adminId}", adminId).with(csrf().asHeader()))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.flash().attributeExists("errorMessage"))
				.andExpect(MockMvcResultMatchers.redirectedUrl("/admin/main"));
	}

	@ParameterizedTest
	@CsvSource({ "1, password, newPassword" })
	void testUpdatePassword_Success(int adminId, String oldPassword, String newPassword) throws Exception {
		Admin resultAdmin = new Admin();
		resultAdmin.setId(1);
		resultAdmin.setHashedPassword(newPassword);

		when(adminService.changeAdminPasswordById(adminId, oldPassword, newPassword)).thenReturn(resultAdmin);

		mockMvc.perform(MockMvcRequestBuilders.post("/admin/update-password").param("adminId", String.valueOf(adminId))
				.param("oldPassword", oldPassword).param("newPassword", newPassword).with(csrf().asHeader()))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"))
				.andExpect(MockMvcResultMatchers.redirectedUrl("/admin/main"));
	}

	@ParameterizedTest
	@CsvSource({ "-1, password, newPassword" })
	void testUpdatePassword_Failure_NoSuchElementException(int adminId, String oldPassword, String newPassword)
			throws Exception {
		when(adminService.changeAdminPasswordById(adminId, oldPassword, newPassword))
				.thenReturn(null);

		mockMvc.perform(MockMvcRequestBuilders.post("/admin/update-password").param("adminId", String.valueOf(adminId))
				.param("oldPassword", oldPassword).param("newPassword", newPassword).with(csrf().asHeader()))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.flash().attributeExists("errorMessage"))
				.andExpect(MockMvcResultMatchers.redirectedUrl("/admin/main"));
	}

	@ParameterizedTest
	@CsvSource({ "1, wrongOldpassword, newPassword" })
	void testUpdatePassword_Failure_WrongOldPassword(int adminId, String wrongOldPassword, String newPassword)
			throws Exception {
		when(adminService.changeAdminPasswordById(adminId, wrongOldPassword, newPassword))
				.thenThrow(IllegalStateException.class);

		mockMvc.perform(MockMvcRequestBuilders.post("/admin/update-password").param("adminId", String.valueOf(adminId))
				.param("oldPassword", wrongOldPassword).param("newPassword", newPassword).with(csrf().asHeader()))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.redirectedUrl("/admin/main"));
	}

	@Test
	void testUpdateAdmin_Success() throws Exception {
		int adminId = 1;
		Admin updatedAdmin = new Admin();
		updatedAdmin.setId(adminId);

		when(adminService.updateAdminById(adminId, updatedAdmin)).thenReturn(updatedAdmin);

		mockMvc.perform(MockMvcRequestBuilders.post("/admin/edit-admin/{adminId}", adminId)
				.flashAttr("admin", updatedAdmin).with(csrf().asHeader()))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"))
				.andExpect(MockMvcResultMatchers.redirectedUrl("/admin/admin-card/" + adminId));
	}

	@Test
	void testUpdateAdmin_Failure() throws Exception {
		int adminId = 1;
		Admin updatedAdmin = new Admin();
		updatedAdmin.setId(adminId);

		when(adminService.updateAdminById(adminId, updatedAdmin)).thenReturn(null);

		mockMvc.perform(MockMvcRequestBuilders.post("/admin/edit-admin/{adminId}", adminId)
				.flashAttr("admin", updatedAdmin).with(csrf().asHeader()))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.flash().attributeExists("errorMessage"))
				.andExpect(MockMvcResultMatchers.redirectedUrl("/admin/admin-card/" + adminId));
	}

	@Test
	void testGetAllAdmin() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/admin/admin-list"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.model().attributeExists("admins"))
				.andExpect(MockMvcResultMatchers.view().name("admin/admin-list"));
	}

	@Test
	void testDeleteAdmin() throws Exception {
		int adminId = 1;
		mockMvc.perform(MockMvcRequestBuilders.post("/admin/delete/{adminId}", adminId).with(csrf().asHeader()))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"))
				.andExpect(MockMvcResultMatchers.redirectedUrl("/admin/admin-list"));
	}

	@Test
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
	void testOpenAdminCard_WhenAdminDoesNotExist() throws Exception {
		int adminId = 999;

		when(adminService.findAdminById(adminId)).thenReturn(Optional.empty());

		mockMvc.perform(MockMvcRequestBuilders.get("/admin/admin-card/{adminId}", adminId))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.redirectedUrl("/admin/admin-list"));
	}

	@Test
	void testShowCreateAdminForm() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/admin/create-admin"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("admin/create-admin"));
	}

	@Test
	void testCreateAdmin() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/admin/create-admin").with(csrf().asHeader()))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.flash().attributeCount(1))
				.andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
	}

}
