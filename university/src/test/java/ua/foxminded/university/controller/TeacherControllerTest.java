package ua.foxminded.university.controller;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ua.foxminded.university.dao.entities.Course;
import ua.foxminded.university.dao.entities.Department;
import ua.foxminded.university.dao.entities.Faculty;
import ua.foxminded.university.dao.entities.Teacher;
import ua.foxminded.university.dao.service.CourseService;
import ua.foxminded.university.dao.service.DepartmentService;
import ua.foxminded.university.dao.service.TeacherService;
import ua.foxminded.university.validation.ControllerBindingValidator;

@WebMvcTest({ TeacherController.class, ControllerBindingValidator.class })
@ActiveProfiles("test-container")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class TeacherControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private TeacherService teacherService;

	@MockBean
	private DepartmentService departmentService;

	@MockBean
	private CourseService courseService;

	@Test
	@WithMockUser(roles = "ADMIN")
	void testShowCreateTeacherForm() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/teacher/create-teacher"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("teacher/create-teacher"));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void testCreateTeacher() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/teacher/create-teacher").with(csrf().asHeader()))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.flash().attributeCount(1))
				.andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void testOpenTeacherCard_WhenTeacherExists() throws Exception {
		Faculty faculty = new Faculty("Grifindor");
		Department department = new Department("Department A", faculty);
		Teacher teacher = new Teacher("Madam", "Trix", true, "trix@mail.com", "1234", department);
		teacher.setId(1);

		when(teacherService.findTeacherById(teacher.getId())).thenReturn(Optional.of(teacher));

		mockMvc.perform(MockMvcRequestBuilders.get("/teacher/teacher-card/{teacherId}", teacher.getId()))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("teacher/teacher-card"))
				.andExpect(MockMvcResultMatchers.model().attributeExists("teacher"))
				.andExpect(MockMvcResultMatchers.model().attributeExists("courses"))
				.andExpect(MockMvcResultMatchers.model().attribute("teacher", Matchers.sameInstance(teacher)));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void testOpenTeacherCard_WhenTeacherDoesNotExist() throws Exception {
		int teacherId = 999;

		when(teacherService.findTeacherById(teacherId)).thenReturn(Optional.empty());

		mockMvc.perform(MockMvcRequestBuilders.get("/teacher/teacher-card/{teacherId}", teacherId))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.redirectedUrl("/teacher/teacher-list"));
	}

	@Test
	@WithMockUser(roles = { "ADMIN", "STUDENT", "TEACHER", "STAFF" })
	void testGetAllTeachersList() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/teacher/teacher-list"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.model().attributeExists("teachers"))
				.andExpect(MockMvcResultMatchers.view().name("teacher/teacher-list"));
	}

	@Test
	@WithMockUser(roles = { "ADMIN", "STUDENT", "TEACHER", "STAFF" })
	void testSearchTeachers_WhenSearchTypeIsTeacher() throws Exception {
		Faculty faculty = new Faculty("Grifindor");
		Department department = new Department("Department A", faculty);
		Teacher teacher = new Teacher("Madam", "Trix", true, "trix@mail.com", "1234", department);
		teacher.setId(1);

		when(teacherService.findTeacherById(teacher.getId())).thenReturn(Optional.of(teacher));

		mockMvc.perform(MockMvcRequestBuilders.get("/teacher/search-result").param("searchType", "teacher")
				.param("teacherId", String.valueOf(teacher.getId()))).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("teacher/teacher-list"))
				.andExpect(MockMvcResultMatchers.model().attributeExists("teachers"))
				.andExpect(MockMvcResultMatchers.model().attribute("teachers", Matchers.hasSize(1)))
				.andExpect(MockMvcResultMatchers.model().attribute("teachers", Matchers.contains(teacher)));
	}

	@Test
	@WithMockUser(roles = { "ADMIN", "STUDENT", "TEACHER", "STAFF" })
	void testSearchTeachers_WhenSearchTypeIsFirstNameAndLastName() throws Exception {
		Faculty faculty = new Faculty("Grifindor");
		Department department = new Department("Department A", faculty);
		Teacher teacher = new Teacher("Madam", "Trix", true, "trix@mail.com", "1234", department);
		teacher.setId(1);

		when(teacherService.findTeacherByName(teacher.getFirstName(), teacher.getLastName()))
				.thenReturn(Collections.singletonList(teacher));

		mockMvc.perform(MockMvcRequestBuilders.get("/teacher/search-result").param("searchType", "firstNameAndLastName")
				.param("firstName", teacher.getFirstName()).param("lastName", teacher.getLastName()))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("teacher/teacher-list"))
				.andExpect(MockMvcResultMatchers.model().attributeExists("teachers"))
				.andExpect(MockMvcResultMatchers.model().attribute("teachers", Matchers.hasSize(1)))
				.andExpect(MockMvcResultMatchers.model().attribute("teachers", Matchers.contains(teacher)));
	}

	@Test
	@WithMockUser(roles = { "ADMIN", "STUDENT", "TEACHER", "STAFF" })
	void testSearchTeachers_WhenSearchTypeIsFaculty() throws Exception {
		Faculty faculty = new Faculty("Grifindor");
		Department department = new Department("Department A", faculty);
		Teacher teacher1 = new Teacher("Madam", "Trix", true, "trix@mail.com", "1234", department);
		teacher1.setId(1);

		Teacher teacher2 = new Teacher("Gilderoy", "Lockhard", true, "gold@mail.com", "1234", department);
		teacher2.setId(2);
		List<Teacher> teachers = Arrays.asList(teacher1, teacher2);

		when(teacherService.findAllByFacultyName(faculty.getFacultyName())).thenReturn(teachers);

		mockMvc.perform(MockMvcRequestBuilders.get("/teacher/search-result").param("searchType", "faculty")
				.param("facultyName", faculty.getFacultyName())).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("teacher/teacher-list"))
				.andExpect(MockMvcResultMatchers.model().attributeExists("teachers"))
				.andExpect(MockMvcResultMatchers.model().attribute("teachers", Matchers.hasSize(2)))
				.andExpect(MockMvcResultMatchers.model().attribute("teachers", Matchers.contains(teacher1, teacher2)));
	}

	@Test
	@WithMockUser(roles = { "ADMIN", "STUDENT", "TEACHER", "STAFF" })
	void testSearchTeachers_WhenSearchTypeIsDepartment() throws Exception {
		Faculty faculty = new Faculty("Grifindor");
		faculty.setId(1);
		Department department = new Department("Department A", faculty);
		department.setId(1);
		Teacher teacher1 = new Teacher("Madam", "Trix", true, "trix@mail.com", "1234", department);
		teacher1.setId(1);
		Teacher teacher2 = new Teacher("Gilderoy", "Lockhard", true, "gold@mail.com", "1234", department);
		teacher2.setId(2);

		List<Teacher> teachers = Arrays.asList(teacher1, teacher2);

		when(teacherService.findAllByDepartmentIdAndDepartmentFacultyId(department.getId(), faculty.getId()))
				.thenReturn(teachers);

		mockMvc.perform(MockMvcRequestBuilders.get("/teacher/search-result").param("searchType", "department")
				.param("departmentId", String.valueOf(department.getId()))
				.param("facultyId", String.valueOf(faculty.getId()))).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("teacher/teacher-list"))
				.andExpect(MockMvcResultMatchers.model().attributeExists("teachers"))
				.andExpect(MockMvcResultMatchers.model().attribute("teachers", Matchers.hasSize(2)))
				.andExpect(MockMvcResultMatchers.model().attribute("teachers", Matchers.contains(teacher1, teacher2)));
	}

	@Test
	@WithMockUser(roles = { "ADMIN", "STUDENT", "TEACHER", "STAFF" })
	void testSearchTeachers_WhenSearchTypeIsCourse() throws Exception {
		String courseName = "Brooms";
		Faculty faculty = new Faculty("Grifindor");
		faculty.setId(1);
		Department department = new Department("Department A", faculty);
		department.setId(1);
		Teacher teacher = new Teacher("Madam", "Trix", true, "trix@mail.com", "1234", department);
		teacher.setId(1);

		List<Teacher> teachers = Collections.singletonList(teacher);

		when(teacherService.findTeachersRelatedToCourse(courseName)).thenReturn(teachers);

		mockMvc.perform(MockMvcRequestBuilders.get("/teacher/search-result").param("searchType", "course")
				.param("courseName", courseName).param("departmentId", String.valueOf(department.getId())))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("teacher/teacher-list"))
				.andExpect(MockMvcResultMatchers.model().attributeExists("teachers"))
				.andExpect(MockMvcResultMatchers.model().attribute("teachers", Matchers.contains(teacher)));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void testDeleteTeacher() throws Exception {
		int teacherId = 1;
		mockMvc.perform(MockMvcRequestBuilders.post("/teacher/delete/{teachertId}", teacherId).with(csrf().asHeader()))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"))
				.andExpect(MockMvcResultMatchers.redirectedUrl("/teacher/teacher-list"));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void testRemoveTeacherFromCourse() throws Exception {
		int teacherId = 1;
		Course course = new Course("Herbology");

		mockMvc.perform(MockMvcRequestBuilders
				.post("/teacher/remove-course/{teacherId}/{courseName}", teacherId, course.getCourseName())
				.with(csrf().asHeader())).andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"))
				.andExpect(MockMvcResultMatchers.redirectedUrl("/teacher/teacher-card/" + teacherId));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void testAddTeacherToTheCourse_Success() throws Exception {
		int teacherId = 1;
		String courseName = "Math";

		mockMvc.perform(MockMvcRequestBuilders.post("/teacher/assign-course")
				.param("teacherId", String.valueOf(teacherId)).param("courseName", courseName).with(csrf().asHeader()))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"))
				.andExpect(MockMvcResultMatchers.redirectedUrl("/teacher/teacher-card/" + teacherId));

		verify(teacherService, times(1)).addTeacherToTheCourse(teacherId, courseName);
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void testAddTeacherToTheCourse_Failure_IllegalStateException() throws Exception {
		int teacherId = 1;
		String courseName = "Math";

		doThrow(new IllegalStateException("Error message")).when(teacherService).addTeacherToTheCourse(teacherId,
				courseName);

		mockMvc.perform(MockMvcRequestBuilders.post("/teacher/assign-course")
				.param("teacherId", String.valueOf(teacherId)).param("courseName", courseName).with(csrf().asHeader()))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.flash().attributeExists("errorMessage"))
				.andExpect(MockMvcResultMatchers.redirectedUrl("/teacher/teacher-card/" + teacherId));

		verify(teacherService, times(1)).addTeacherToTheCourse(teacherId, courseName);
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void testAddTeacherToTheCourse_Failure_NoSuchElementException() throws Exception {
		int studentId = 1;
		String courseName = "Math";

		doThrow(new NoSuchElementException("Error message")).when(teacherService).addTeacherToTheCourse(studentId,
				courseName);

		mockMvc.perform(MockMvcRequestBuilders.post("/teacher/assign-course")
				.param("teacherId", String.valueOf(studentId)).param("courseName", courseName).with(csrf().asHeader()))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.flash().attributeExists("errorMessage"))
				.andExpect(MockMvcResultMatchers.redirectedUrl("/teacher/teacher-card/" + studentId));

		verify(teacherService, times(1)).addTeacherToTheCourse(studentId, courseName);
	}

}
