package ua.foxminded.university.controller.admin;

import static org.mockito.Mockito.when;
import java.util.Optional;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ua.foxminded.university.dao.entities.Faculty;
import ua.foxminded.university.dao.service.FacultyService;
import ua.foxminded.university.validation.ControllerBindingValidator;

@WebMvcTest({ AdminFacultyController.class, ControllerBindingValidator.class })
@ActiveProfiles("test-container")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class AdminFacultyControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private FacultyService facultyService;

	@Test
	void testGetAllFacultyListAsAdmin() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/admin/faculty/edit-faculty-list"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.model().attributeExists("faculties"))
				.andExpect(MockMvcResultMatchers.view().name("admin/faculty/edit-faculty-list"));
	}

	@Test
	void testDeleteFaculty() throws Exception {
		int facultyId = 1;
		mockMvc.perform(MockMvcRequestBuilders.post("/admin/faculty/delete/{facultyId}", facultyId))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"))
				.andExpect(MockMvcResultMatchers.redirectedUrl("/admin/faculty/edit-faculty-list"));
	}

	@Test
	void testShowCreateFacultyForm() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/admin/faculty/create-faculty"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("admin/faculty/create-faculty"));
	}

	@Test
	void testCreateFaculty() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/admin/faculty/create-faculty"))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.flash().attributeCount(1))
				.andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
	}

	@Test
	void testOpenFacultyCard_WhenFacultyExists() throws Exception {
		Faculty faculty = new Faculty("Faculty A");
		faculty.setId(1);

		when(facultyService.findFacultyById(faculty.getId())).thenReturn(Optional.of(faculty));

		mockMvc.perform(MockMvcRequestBuilders.get("/admin/faculty/faculty-card/{facultyId}", faculty.getId()))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("admin/faculty/faculty-card"))
				.andExpect(MockMvcResultMatchers.model().attributeExists("faculty"))
				.andExpect(MockMvcResultMatchers.model().attribute("faculty", Matchers.sameInstance(faculty)));
	}

	@Test
	void testOpenFacultyCard_WhenFacultyDoesNotExist() throws Exception {
		int facultyId = 999;

		when(facultyService.findFacultyById(facultyId)).thenReturn(Optional.empty());

		mockMvc.perform(MockMvcRequestBuilders.get("/admin/faculty/faculty-card/{facultyId}", facultyId))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.redirectedUrl("/admin/faculty/edit-faculty-list"));
	}
}
