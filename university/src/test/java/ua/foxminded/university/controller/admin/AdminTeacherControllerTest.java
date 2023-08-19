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

}
