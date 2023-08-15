package ua.foxminded.university.controller.admin;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ua.foxminded.university.dao.service.DepartmentService;
import ua.foxminded.university.dao.service.FacultyService;

@WebMvcTest(AdminDepartmentController.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test-container")
class AdminDepartmentControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private DepartmentService departmentService;

	@MockBean
	private FacultyService facultyService;

	@Test
	void testGetAllDepartmentListAsAdmin() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/admin/department/edit-department-list"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.model().attributeExists("departments"))
				.andExpect(MockMvcResultMatchers.view().name("admin/department/edit-department-list"));
	}

	@Test
	void testDeleteDepartment() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/admin/department/delete/{departmentId}", 1))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"))
				.andExpect(MockMvcResultMatchers.redirectedUrl("/admin/department/edit-department-list"));
	}

	@Test
	void testShowCreateDepartmentForm() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/admin/department/create-department"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("admin/department/create-department"));
	}

	@Test
	void testCreateDepartment() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/admin/department/create-department"))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.flash().attributeCount(1))
				.andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
	}
}
