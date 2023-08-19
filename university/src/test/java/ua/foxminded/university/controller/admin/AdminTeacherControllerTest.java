package ua.foxminded.university.controller.admin;

import static org.mockito.Mockito.when;
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
import ua.foxminded.university.dao.entities.Department;
import ua.foxminded.university.dao.entities.Faculty;
import ua.foxminded.university.dao.entities.Teacher;
import ua.foxminded.university.dao.service.CourseService;
import ua.foxminded.university.dao.service.DepartmentService;
import ua.foxminded.university.dao.service.TeacherService;

@WebMvcTest(AdminTeacherController.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test-container")
class AdminTeacherControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private TeacherService teacherService;

	@MockBean
	private DepartmentService departmentService;

	@MockBean
	private CourseService courseService;

	@Test
	void testShowCreateTeacherForm() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/admin/teacher/create-teacher"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("admin/teacher/create-teacher"));
	}

	@Test
	void testCreateTeacher() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/admin/teacher/create-teacher"))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.flash().attributeCount(1))
				.andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
	}

	@Test
	void testOpenTeacherCard_WhenTeacherExists() throws Exception {
		Faculty faculty = new Faculty("Grifindor");

		Department department = new Department("Department A", faculty);
		Teacher teacher = new Teacher("Madam", "Trix", true, "trix@mail.com", "1234", department);
		teacher.setId(1);

		when(teacherService.findTeacherById(teacher.getId())).thenReturn(Optional.of(teacher));

		mockMvc.perform(MockMvcRequestBuilders.get("/admin/teacher/teacher-card/{teacherId}", teacher.getId()))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("admin/teacher/teacher-card"))
				.andExpect(MockMvcResultMatchers.model().attributeExists("teacher"))
				.andExpect(MockMvcResultMatchers.model().attributeExists("courses"))
				.andExpect(MockMvcResultMatchers.model().attribute("teacher", Matchers.sameInstance(teacher)));
	}

	@Test
	void testOpenTeacherCard_WhenTeacherDoesNotExist() throws Exception {
		int teacherId = 999;

		when(teacherService.findTeacherById(teacherId)).thenReturn(Optional.empty());

		mockMvc.perform(MockMvcRequestBuilders.get("/admin/teacher/teacher-card/{teacherId}", teacherId))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.redirectedUrl("/admin/teacher/edit-teacher-list"));
	}
}
