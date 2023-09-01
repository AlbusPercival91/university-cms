package ua.foxminded.university.controller;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
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
import ua.foxminded.university.dao.entities.Faculty;
import ua.foxminded.university.dao.service.FacultyService;
import ua.foxminded.university.validation.ControllerBindingValidator;

@WebMvcTest({ FacultyController.class, ControllerBindingValidator.class })
@ActiveProfiles("test-container")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class FacultyControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private FacultyService facultyService;

	@Test
	@WithMockUser(roles = { "ADMIN", "STUDENT", "TEACHER", "STAFF" })
	void testGetAllFacultyList() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/faculty/faculty-list"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.model().attributeExists("faculties"))
				.andExpect(MockMvcResultMatchers.view().name("faculty/faculty-list"));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void testDeleteFaculty() throws Exception {
		int facultyId = 1;
		mockMvc.perform(MockMvcRequestBuilders.post("/faculty/delete/{facultyId}", facultyId).with(csrf().asHeader()))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"))
				.andExpect(MockMvcResultMatchers.redirectedUrl("/faculty/faculty-list"));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void testShowCreateFacultyForm() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/faculty/create-faculty"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("faculty/create-faculty"));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void testCreateFaculty() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/faculty/create-faculty").with(csrf().asHeader()))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.flash().attributeCount(1))
				.andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void testOpenFacultyCard_WhenFacultyExists() throws Exception {
		Faculty faculty = new Faculty("Faculty A");
		faculty.setId(1);

		when(facultyService.findFacultyById(faculty.getId())).thenReturn(Optional.of(faculty));

		mockMvc.perform(MockMvcRequestBuilders.get("/faculty/faculty-card/{facultyId}", faculty.getId()))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("faculty/faculty-card"))
				.andExpect(MockMvcResultMatchers.model().attributeExists("faculty"))
				.andExpect(MockMvcResultMatchers.model().attribute("faculty", Matchers.sameInstance(faculty)));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void testOpenFacultyCard_WhenFacultyDoesNotExist() throws Exception {
		int facultyId = 999;

		when(facultyService.findFacultyById(facultyId)).thenReturn(Optional.empty());

		mockMvc.perform(MockMvcRequestBuilders.get("/faculty/faculty-card/{facultyId}", facultyId))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.redirectedUrl("/faculty/faculty-list"));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void testUpdateFaculty_Success() throws Exception {
		int facultyId = 1;
		Faculty updatedFaculty = new Faculty();
		updatedFaculty.setId(facultyId);

		when(facultyService.updateFacultyById(facultyId, updatedFaculty)).thenReturn(updatedFaculty);

		mockMvc.perform(MockMvcRequestBuilders.post("/faculty/edit-faculty/{facultyId}", facultyId)
				.flashAttr("faculty", updatedFaculty).with(csrf().asHeader()))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"))
				.andExpect(MockMvcResultMatchers.redirectedUrl("/faculty/faculty-card/" + facultyId));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void testUpdateFaculty_Failure() throws Exception {
		int facultyId = 1;
		Faculty updatedFaculty = new Faculty();
		updatedFaculty.setId(facultyId);

		when(facultyService.updateFacultyById(facultyId, updatedFaculty)).thenReturn(null);

		mockMvc.perform(MockMvcRequestBuilders.post("/faculty/edit-faculty/{facultyId}", facultyId)
				.flashAttr("faculty", updatedFaculty).with(csrf().asHeader()))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.flash().attributeExists("errorMessage"))
				.andExpect(MockMvcResultMatchers.redirectedUrl("/faculty/faculty-card/" + facultyId));
	}
}
