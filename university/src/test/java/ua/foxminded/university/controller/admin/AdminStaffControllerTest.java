package ua.foxminded.university.controller.admin;

import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ua.foxminded.university.dao.entities.Staff;
import ua.foxminded.university.dao.service.StaffService;

@WebMvcTest(AdminStaffController.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test-container")
class AdminStaffControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private StaffService staffService;

	@Test
	void testGetAllStaffListAsAdmin() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/admin/staff/edit-staff-list"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.model().attributeExists("staff"))
				.andExpect(MockMvcResultMatchers.view().name("admin/staff/edit-staff-list"));
	}

	@Test
	void testDeleteStaff() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/admin/staff/delete/{staffId}", 1))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"))
				.andExpect(MockMvcResultMatchers.redirectedUrl("/admin/staff/edit-staff-list"));
	}

	@Test
	void testShowCreateStaffForm() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/admin/staff/create-staff"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("admin/staff/create-staff"));
	}

	@Test
	void testCreateStaff() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/admin/staff/create-staff"))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.flash().attributeCount(1))
				.andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
	}

	@Test
	void testSearchStaffAsAdmin_WhenSearchTypeIsStaff() throws Exception {
		Staff staff = new Staff("Argus", "Filtch", true, "argus@mail.com", "1234", "Techical manager", null);
		staff.setId(1);

		when(staffService.findStaffById(1)).thenReturn(Optional.of(staff));

		mockMvc.perform(MockMvcRequestBuilders.get("/admin/staff/search-result").param("searchType", "staff")
				.param("staffId", "1")).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("admin/staff/edit-staff-list"))
				.andExpect(MockMvcResultMatchers.model().attributeExists("staff"))
				.andExpect(MockMvcResultMatchers.model().attribute("staff", Matchers.hasSize(1)))
				.andExpect(MockMvcResultMatchers.model().attribute("staff", Matchers.contains(staff)));
	}

	@Test
	void testSearchStaffAsAdmin_WhenSearchTypeIsFirstNameAndLastName() throws Exception {
		Staff staff = new Staff("Argus", "Filtch", true, "argus@mail.com", "1234", "Techical manager", null);
		staff.setId(1);

		when(staffService.findStaffByName(staff.getFirstName(), staff.getLastName()))
				.thenReturn(Collections.singletonList(staff));

		mockMvc.perform(
				MockMvcRequestBuilders.get("/admin/staff/search-result").param("searchType", "firstNameAndLastName")
						.param("firstName", staff.getFirstName()).param("lastName", staff.getLastName()))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("admin/staff/edit-staff-list"))
				.andExpect(MockMvcResultMatchers.model().attributeExists("staff"))
				.andExpect(MockMvcResultMatchers.model().attribute("staff", Matchers.hasSize(1)))
				.andExpect(MockMvcResultMatchers.model().attribute("staff", Matchers.contains(staff)));
	}

	@Test
	void testSearchStaffAsAdmin_WhenSearchTypeIsPosition() throws Exception {
		Staff staff1 = new Staff("Argus", "Filtch", true, "argus@mail.com", "1234", "Techical manager", null);
		staff1.setId(1);
		Staff staff2 = new Staff("Rubeous", "Hagrid", true, "hagrid@mail.com", "1234", "Techical manager", null);
		staff2.setId(1);
		List<Staff> staff = Arrays.asList(staff1, staff2);

		when(staffService.findStaffByPosition(staff1.getPosition())).thenReturn(staff);

		mockMvc.perform(MockMvcRequestBuilders.get("/admin/staff/search-result").param("searchType", "position")
				.param("position", staff1.getPosition())).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("admin/staff/edit-staff-list"))
				.andExpect(MockMvcResultMatchers.model().attributeExists("staff"))
				.andExpect(MockMvcResultMatchers.model().attribute("staff", Matchers.hasSize(2)))
				.andExpect(MockMvcResultMatchers.model().attribute("staff", Matchers.contains(staff1, staff2)));
	}

	@Test
	void testOpenStaffCard_WhenStaffExists() throws Exception {
		Staff staff = new Staff("Argus", "Filtch", true, "argus@mail.com", "1234", "Techical manager", null);
		staff.setId(1);

		when(staffService.findStaffById(staff.getId())).thenReturn(Optional.of(staff));

		mockMvc.perform(MockMvcRequestBuilders.get("/admin/staff/staff-card/{staffId}", staff.getId()))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("admin/staff/staff-card"))
				.andExpect(MockMvcResultMatchers.model().attributeExists("staff"))
				.andExpect(MockMvcResultMatchers.model().attribute("staff", Matchers.sameInstance(staff)));
	}

	@Test
	void testOpenStaffCard_WhenStaffDoesNotExist() throws Exception {
		int staffId = 999;

		when(staffService.findStaffById(staffId)).thenReturn(Optional.empty());

		mockMvc.perform(MockMvcRequestBuilders.get("/admin/staff/staff-card/{staffId}", staffId))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.redirectedUrl("/admin/staff/edit-staff-list"));
	}
}
