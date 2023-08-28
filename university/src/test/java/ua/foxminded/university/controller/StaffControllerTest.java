package ua.foxminded.university.controller;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
import ua.foxminded.university.dao.entities.Staff;
import ua.foxminded.university.dao.service.StaffService;
import ua.foxminded.university.validation.ControllerBindingValidator;

@WebMvcTest({ StaffController.class, ControllerBindingValidator.class })
@ActiveProfiles("test-container")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class StaffControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private StaffService staffService;

	@Test
	@WithMockUser(roles = { "ADMIN", "STUDENT", "TEACHER", "STAFF" })
	void testGetAllStaffList() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/staff/staff-list"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.model().attributeExists("staff"))
				.andExpect(MockMvcResultMatchers.view().name("staff/staff-list"));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void testDeleteStaff() throws Exception {
		int staffId = 1;
		mockMvc.perform(MockMvcRequestBuilders.post("/staff/delete/{staffId}", staffId).with(csrf().asHeader()))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"))
				.andExpect(MockMvcResultMatchers.redirectedUrl("/staff/staff-list"));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void testShowCreateStaffForm() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/staff/create-staff"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("staff/create-staff"));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void testCreateStaff() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/staff/create-staff").with(csrf().asHeader()))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.flash().attributeCount(1))
				.andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
	}

	@Test
	@WithMockUser(roles = { "ADMIN", "STUDENT", "TEACHER", "STAFF" })
	void testSearchStaff_WhenSearchTypeIsStaff() throws Exception {
		Staff staff = new Staff("Argus", "Filtch", true, "argus@mail.com", "1234", "Techical manager", null);
		staff.setId(1);

		when(staffService.findStaffById(staff.getId())).thenReturn(Optional.of(staff));

		mockMvc.perform(MockMvcRequestBuilders.get("/staff/search-result").param("searchType", "staff").param("staffId",
				String.valueOf(staff.getId()))).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("staff/staff-list"))
				.andExpect(MockMvcResultMatchers.model().attributeExists("staff"))
				.andExpect(MockMvcResultMatchers.model().attribute("staff", Matchers.hasSize(1)))
				.andExpect(MockMvcResultMatchers.model().attribute("staff", Matchers.contains(staff)));
	}

	@Test
	@WithMockUser(roles = { "ADMIN", "STUDENT", "TEACHER", "STAFF" })
	void testSearchStaff_WhenSearchTypeIsFirstNameAndLastName() throws Exception {
		Staff staff = new Staff("Argus", "Filtch", true, "argus@mail.com", "1234", "Techical manager", null);
		staff.setId(1);

		when(staffService.findStaffByName(staff.getFirstName(), staff.getLastName()))
				.thenReturn(Collections.singletonList(staff));

		mockMvc.perform(MockMvcRequestBuilders.get("/staff/search-result").param("searchType", "firstNameAndLastName")
				.param("firstName", staff.getFirstName()).param("lastName", staff.getLastName()))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("staff/staff-list"))
				.andExpect(MockMvcResultMatchers.model().attributeExists("staff"))
				.andExpect(MockMvcResultMatchers.model().attribute("staff", Matchers.hasSize(1)))
				.andExpect(MockMvcResultMatchers.model().attribute("staff", Matchers.contains(staff)));
	}

	@Test
	@WithMockUser(roles = { "ADMIN", "STUDENT", "TEACHER", "STAFF" })
	void testSearchStaff_WhenSearchTypeIsPosition() throws Exception {
		Staff staff1 = new Staff("Argus", "Filtch", true, "argus@mail.com", "1234", "Techical manager", null);
		staff1.setId(1);
		Staff staff2 = new Staff("Rubeous", "Hagrid", true, "hagrid@mail.com", "1234", "Techical manager", null);
		staff2.setId(2);
		List<Staff> staff = Arrays.asList(staff1, staff2);

		when(staffService.findStaffByPosition(staff1.getPosition())).thenReturn(staff);

		mockMvc.perform(MockMvcRequestBuilders.get("/staff/search-result").param("searchType", "position")
				.param("position", staff1.getPosition())).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("staff/staff-list"))
				.andExpect(MockMvcResultMatchers.model().attributeExists("staff"))
				.andExpect(MockMvcResultMatchers.model().attribute("staff", Matchers.hasSize(2)))
				.andExpect(MockMvcResultMatchers.model().attribute("staff", Matchers.contains(staff1, staff2)));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void testOpenStaffCard_WhenStaffExists() throws Exception {
		Staff staff = new Staff("Argus", "Filtch", true, "argus@mail.com", "1234", "Techical manager", null);
		staff.setId(1);

		when(staffService.findStaffById(staff.getId())).thenReturn(Optional.of(staff));

		mockMvc.perform(MockMvcRequestBuilders.get("/staff/staff-card/{staffId}", staff.getId()))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("staff/staff-card"))
				.andExpect(MockMvcResultMatchers.model().attributeExists("staff"))
				.andExpect(MockMvcResultMatchers.model().attribute("staff", Matchers.sameInstance(staff)));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void testOpenStaffCard_WhenStaffDoesNotExist() throws Exception {
		int staffId = 999;

		when(staffService.findStaffById(staffId)).thenReturn(Optional.empty());

		mockMvc.perform(MockMvcRequestBuilders.get("/staff/staff-card/{staffId}", staffId))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.redirectedUrl("/staff/staff-list"));
	}
}
