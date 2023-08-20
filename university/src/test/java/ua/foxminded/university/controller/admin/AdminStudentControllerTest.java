package ua.foxminded.university.controller.admin;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
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
import ua.foxminded.university.dao.entities.Course;
import ua.foxminded.university.dao.entities.Faculty;
import ua.foxminded.university.dao.entities.Group;
import ua.foxminded.university.dao.entities.Student;
import ua.foxminded.university.dao.service.CourseService;
import ua.foxminded.university.dao.service.StudentService;

@WebMvcTest(AdminStudentController.class)
@ActiveProfiles("test-container")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
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

	@Test
	void testRemoveStudentFromCourse() throws Exception {
		int studentId = 1;
		Course course = new Course("Herbology");

		mockMvc.perform(MockMvcRequestBuilders.post("/admin/student/remove-course/{studentId}/{courseName}", 1,
				course.getCourseName())).andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"))
				.andExpect(MockMvcResultMatchers.redirectedUrl("/admin/student/student-card/" + studentId));
	}

	@Test
	void testAddStudentToTheCourse_Success() throws Exception {
		int studentId = 1;
		String courseName = "Math";

		mockMvc.perform(MockMvcRequestBuilders.post("/admin/student/assign-course")
				.param("studentId", String.valueOf(studentId)).param("courseName", courseName))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"))
				.andExpect(MockMvcResultMatchers.redirectedUrl("/admin/student/student-card/" + studentId));

		verify(studentService, times(1)).addStudentToTheCourse(studentId, courseName);
	}

	@Test
	void testAddStudentToTheCourse_Failure_IllegalStateException() throws Exception {
		int studentId = 1;
		String courseName = "Math";

		doThrow(new IllegalStateException("Error message")).when(studentService).addStudentToTheCourse(studentId,
				courseName);

		mockMvc.perform(MockMvcRequestBuilders.post("/admin/student/assign-course")
				.param("studentId", String.valueOf(studentId)).param("courseName", courseName))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.flash().attributeExists("errorMessage"))
				.andExpect(MockMvcResultMatchers.redirectedUrl("/admin/student/student-card/" + studentId));

		verify(studentService, times(1)).addStudentToTheCourse(studentId, courseName);
	}

	@Test
	void testAddStudentToTheCourse_Failure_NoSuchElementException() throws Exception {
		int studentId = 1;
		String courseName = "Math";

		doThrow(new NoSuchElementException("Error message")).when(studentService).addStudentToTheCourse(studentId,
				courseName);

		mockMvc.perform(MockMvcRequestBuilders.post("/admin/student/assign-course")
				.param("studentId", String.valueOf(studentId)).param("courseName", courseName))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.flash().attributeExists("errorMessage"))
				.andExpect(MockMvcResultMatchers.redirectedUrl("/admin/student/student-card/" + studentId));

		verify(studentService, times(1)).addStudentToTheCourse(studentId, courseName);
	}

	@Test
	void testSearchStudentAsAdmin_WhenSearchTypeIsStudent() throws Exception {
		Faculty facultyGrifindor = new Faculty("Grifindor");
		Group group = new Group("Group A", facultyGrifindor);
		Student student = new Student("Harry", "Potter", true, "potter@mail.com", "1234", group);
		student.setId(1);

		when(studentService.findStudentById(1)).thenReturn(Optional.of(student));

		mockMvc.perform(MockMvcRequestBuilders.get("/admin/student/search-result").param("searchType", "student")
				.param("studentId", "1")).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("admin/student/edit-student-list"))
				.andExpect(MockMvcResultMatchers.model().attributeExists("students"))
				.andExpect(MockMvcResultMatchers.model().attribute("students", Matchers.hasSize(1)))
				.andExpect(MockMvcResultMatchers.model().attribute("students", Matchers.contains(student)));
	}

	@Test
	void testSearchStudentAsAdmin_WhenSearchTypeIsFirstNameAndLastName() throws Exception {
		Faculty facultyGrifindor = new Faculty("Grifindor");
		Group group = new Group("Group A", facultyGrifindor);
		Student student = new Student("Harry", "Potter", true, "potter@mail.com", "1234", group);
		student.setId(1);

		when(studentService.findStudentByName(student.getFirstName(), student.getLastName()))
				.thenReturn(Collections.singletonList(student));

		mockMvc.perform(
				MockMvcRequestBuilders.get("/admin/student/search-result").param("searchType", "firstNameAndLastName")
						.param("firstName", student.getFirstName()).param("lastName", student.getLastName()))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("admin/student/edit-student-list"))
				.andExpect(MockMvcResultMatchers.model().attributeExists("students"))
				.andExpect(MockMvcResultMatchers.model().attribute("students", Matchers.hasSize(1)))
				.andExpect(MockMvcResultMatchers.model().attribute("students", Matchers.contains(student)));
	}

	@Test
	void testSearchStudentAsAdmin_WhenSearchTypeIsFaculty() throws Exception {
		Faculty faculty = new Faculty("Grifindor");
		Group group = new Group("Group A", faculty);
		Student student1 = new Student("Harry", "Potter", true, "potter@mail.com", "1234", group);
		student1.setId(1);

		Student student2 = new Student("Ron", "Wesley", true, "ronald@mail.com", "1234", group);
		student2.setId(2);
		List<Student> students = Arrays.asList(student1, student2);

		when(studentService.findAllByGroupFacultyFacultyName(faculty.getFacultyName())).thenReturn(students);

		mockMvc.perform(MockMvcRequestBuilders.get("/admin/student/search-result").param("searchType", "faculty")
				.param("facultyName", faculty.getFacultyName())).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("admin/student/edit-student-list"))
				.andExpect(MockMvcResultMatchers.model().attributeExists("students"))
				.andExpect(MockMvcResultMatchers.model().attribute("students", Matchers.hasSize(2)))
				.andExpect(MockMvcResultMatchers.model().attribute("students", Matchers.contains(student1, student2)));
	}

	@Test
	void testSearchStudentAsAdmin_WhenSearchTypeIsGroup() throws Exception {
		Faculty faculty = new Faculty("Grifindor");
		Group group = new Group("Group A", faculty);
		Student student1 = new Student("Harry", "Potter", true, "potter@mail.com", "1234", group);
		student1.setId(1);

		Student student2 = new Student("Ron", "Wesley", true, "ronald@mail.com", "1234", group);
		student2.setId(2);
		List<Student> students = Arrays.asList(student1, student2);

		when(studentService.findAllByGroupName(group.getGroupName())).thenReturn(students);

		mockMvc.perform(MockMvcRequestBuilders.get("/admin/student/search-result").param("searchType", "group")
				.param("groupName", group.getGroupName())).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("admin/student/edit-student-list"))
				.andExpect(MockMvcResultMatchers.model().attributeExists("students"))
				.andExpect(MockMvcResultMatchers.model().attribute("students", Matchers.hasSize(2)))
				.andExpect(MockMvcResultMatchers.model().attribute("students", Matchers.contains(student1, student2)));
	}

	@Test
	void testShowCreateStudentForm() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/admin/student/create-student"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("admin/student/create-student"));
	}

	@Test
	void testCreateStudent() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/admin/student/create-student"))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.flash().attributeCount(1))
				.andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
	}
}
