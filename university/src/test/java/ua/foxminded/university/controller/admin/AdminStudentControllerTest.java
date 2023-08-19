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
import ua.foxminded.university.dao.entities.Faculty;
import ua.foxminded.university.dao.entities.Group;
import ua.foxminded.university.dao.entities.Student;
import ua.foxminded.university.dao.service.CourseService;
import ua.foxminded.university.dao.service.StudentService;

@WebMvcTest(AdminStudentController.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test-container")
class AdminStudentControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private StudentService studentService;

	@MockBean
	private CourseService courseService;

	@Test
	void testGetAllStudentsListAsAdmin() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/admin/student/edit-student-list"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.model().attributeExists("students"))
				.andExpect(MockMvcResultMatchers.view().name("admin/student/edit-student-list"));
	}

	@Test
	void testDeleteStudent() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/admin/student/delete/{studentId}", 1))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"))
				.andExpect(MockMvcResultMatchers.redirectedUrl("/admin/student/edit-student-list"));
	}

	@Test
	void testOpenStudentCard_WhenStudentExists() throws Exception {
		Faculty facultyGrifindor = new Faculty("Grifindor");
		Group group = new Group("Group A", facultyGrifindor);
		Student student = new Student("Harry", "Potter", true, "potter@mail.com", "1234", group);
		student.setId(1);

		when(studentService.findStudentById(student.getId())).thenReturn(Optional.of(student));

		mockMvc.perform(MockMvcRequestBuilders.get("/admin/student/student-card/{studentId}", student.getId()))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("admin/student/student-card"))
				.andExpect(MockMvcResultMatchers.model().attributeExists("student"))
				.andExpect(MockMvcResultMatchers.model().attributeExists("courses"))
				.andExpect(MockMvcResultMatchers.model().attribute("student", Matchers.sameInstance(student)));
	}

	@Test
	void testOpenStudentCard_WhenStudentDoesNotExist() throws Exception {
		int studentId = 999;

		when(studentService.findStudentById(studentId)).thenReturn(Optional.empty());

		mockMvc.perform(MockMvcRequestBuilders.get("/admin/student/student-card/{studentId}", studentId))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.redirectedUrl("/admin/student/edit-student-list"));
	}
}
