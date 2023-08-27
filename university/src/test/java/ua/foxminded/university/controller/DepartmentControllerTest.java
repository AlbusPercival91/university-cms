package ua.foxminded.university.controller;

import static org.mockito.Mockito.when;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ua.foxminded.university.dao.entities.Department;
import ua.foxminded.university.dao.entities.Faculty;
import ua.foxminded.university.dao.service.DepartmentService;
import ua.foxminded.university.dao.service.FacultyService;
import ua.foxminded.university.validation.ControllerBindingValidator;

@WebMvcTest({ DepartmentController.class, ControllerBindingValidator.class })
@ActiveProfiles("test-container")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class DepartmentControllerTest {

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
		int departmentId = 1;
		mockMvc.perform(MockMvcRequestBuilders.post("/admin/department/delete/{departmentId}", departmentId))
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

	@Test
	void testSearchDepartmentAsAdmin_WhenSearchTypeIsDepartment() throws Exception {
		Faculty faculty = new Faculty("Faculty A");
		Department department = new Department("Department A", faculty);
		department.setId(1);

		when(departmentService.findDepartmentById(department.getId())).thenReturn(Optional.of(department));

		mockMvc.perform(MockMvcRequestBuilders.get("/admin/department/search-result").param("searchType", "department")
				.param("departmentId", String.valueOf(department.getId())))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("admin/department/edit-department-list"))
				.andExpect(MockMvcResultMatchers.model().attributeExists("departments"))
				.andExpect(MockMvcResultMatchers.model().attribute("departments", Matchers.hasSize(1)))
				.andExpect(MockMvcResultMatchers.model().attribute("departments", Matchers.contains(department)));
	}

	@Test
	void testSearchDepartmentAsAdmin_WhenSearchTypeIsName() throws Exception {
		Faculty faculty = new Faculty("Faculty A");
		Department department = new Department("Department A", faculty);
		department.setId(1);

		when(departmentService.findDepartmentByName(department.getName()))
				.thenReturn(Collections.singletonList(department));

		mockMvc.perform(MockMvcRequestBuilders.get("/admin/department/search-result").param("searchType", "name")
				.param("name", department.getName())).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("admin/department/edit-department-list"))
				.andExpect(MockMvcResultMatchers.model().attributeExists("departments"))
				.andExpect(MockMvcResultMatchers.model().attribute("departments", Matchers.hasSize(1)))
				.andExpect(MockMvcResultMatchers.model().attribute("departments", Matchers.contains(department)));
	}

	@Test
	void testSearchDepartmentAsAdmin_WhenSearchTypeIsFaculty() throws Exception {
		Faculty faculty = new Faculty("Faculty A");
		Department department1 = new Department("Department A", faculty);
		department1.setId(1);
		Department department2 = new Department("Department B", faculty);
		department2.setId(2);
		List<Department> departments = Arrays.asList(department1, department2);

		when(departmentService.findAllByFacultyName(faculty.getFacultyName())).thenReturn(departments);

		mockMvc.perform(MockMvcRequestBuilders.get("/admin/department/search-result").param("searchType", "faculty")
				.param("facultyName", faculty.getFacultyName())).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("admin/department/edit-department-list"))
				.andExpect(MockMvcResultMatchers.model().attributeExists("departments"))
				.andExpect(MockMvcResultMatchers.model().attribute("departments", Matchers.hasSize(2)))
				.andExpect(MockMvcResultMatchers.model().attribute("departments",
						Matchers.contains(department1, department2)));
	}

	@Test
	void testOpenDepartmentCard_WhenDepartmentExists() throws Exception {
		Faculty faculty = new Faculty("Faculty A");
		Department department = new Department("Department A", faculty);
		department.setId(1);

		when(departmentService.findDepartmentById(department.getId())).thenReturn(Optional.of(department));

		mockMvc.perform(
				MockMvcRequestBuilders.get("/admin/department/department-card/{departmentId}", department.getId()))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("admin/department/department-card"))
				.andExpect(MockMvcResultMatchers.model().attributeExists("department"))
				.andExpect(MockMvcResultMatchers.model().attribute("department", Matchers.sameInstance(department)));
	}

	@Test
	void testOpenDepartmentCard_WhenDepartmentDoesNotExist() throws Exception {
		int departmentId = 999;

		when(departmentService.findDepartmentById(departmentId)).thenReturn(Optional.empty());

		mockMvc.perform(MockMvcRequestBuilders.get("/admin/department/department-card/{departmentId}", departmentId))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.redirectedUrl("/admin/department/edit-department-list"));
	}
}
