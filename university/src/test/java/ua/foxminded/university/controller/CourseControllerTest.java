package ua.foxminded.university.controller;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import java.util.Arrays;
import java.util.List;
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
import ua.foxminded.university.dao.service.CourseService;
import ua.foxminded.university.validation.ControllerBindingValidator;

@WebMvcTest({ CourseController.class, ControllerBindingValidator.class })
@ActiveProfiles("test-container")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CourseControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private CourseService courseService;

	@Test
	@WithMockUser(roles = { "ADMIN", "STUDENT", "TEACHER", "STAFF" })
	void testGetAllCourseList() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/course/course-list"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.model().attributeExists("courses"))
				.andExpect(MockMvcResultMatchers.view().name("course/course-list"));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void testDeleteCourse() throws Exception {
		int courseId = 1;
		mockMvc.perform(MockMvcRequestBuilders.post("/course/delete/{courseId}", courseId).with(csrf().asHeader()))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"))
				.andExpect(MockMvcResultMatchers.redirectedUrl("/course/course-list"));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void testShowCreateCourseForm() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/course/create-course"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("course/create-course"));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void testCreateCourse() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/course/create-course").with(csrf().asHeader()))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.flash().attributeCount(1))
				.andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
	}

	@Test
	@WithMockUser(roles = { "ADMIN", "STUDENT", "TEACHER", "STAFF" })
	void testSearchCourse_WhenSearchTypeIsCourse() throws Exception {
		Course course = new Course();
		course.setId(1);

		when(courseService.findCourseById(course.getId())).thenReturn(Optional.of(course));

		mockMvc.perform(MockMvcRequestBuilders.get("/course/search-result").param("searchType", "course")
				.param("courseId", String.valueOf(course.getId()))).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("course/course-list"))
				.andExpect(MockMvcResultMatchers.model().attributeExists("courses"))
				.andExpect(MockMvcResultMatchers.model().attribute("courses", Matchers.hasSize(1)))
				.andExpect(MockMvcResultMatchers.model().attribute("courses", Matchers.contains(course)));
	}

	@Test
	@WithMockUser(roles = { "ADMIN", "STUDENT", "TEACHER", "STAFF" })
	void testSearchCourse_WhenSearchTypeIsCourseName() throws Exception {
		Course course = new Course("History of Magic");

		when(courseService.findByCourseName(course.getCourseName())).thenReturn(Optional.of(course));

		mockMvc.perform(MockMvcRequestBuilders.get("/course/search-result").param("searchType", "courseName")
				.param("courseName", course.getCourseName())).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("course/course-list"))
				.andExpect(MockMvcResultMatchers.model().attributeExists("courses"))
				.andExpect(MockMvcResultMatchers.model().attribute("courses", Matchers.hasSize(1)))
				.andExpect(MockMvcResultMatchers.model().attribute("courses", Matchers.contains(course)));
	}

	@Test
	@WithMockUser(roles = { "ADMIN", "STUDENT", "TEACHER", "STAFF" })
	void testSearchCourse_WhenSearchTypeIsTeacher() throws Exception {
		int teacherId = 1;
		Course course1 = new Course("Course A");
		Course course2 = new Course("Course B");
		List<Course> courses = Arrays.asList(course1, course2);

		when(courseService.findCoursesRelatedToTeacher(teacherId)).thenReturn(courses);

		mockMvc.perform(MockMvcRequestBuilders.get("/course/search-result").param("searchType", "teacher")
				.param("teacherId", String.valueOf(teacherId))).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("course/course-list"))
				.andExpect(MockMvcResultMatchers.model().attributeExists("courses"))
				.andExpect(MockMvcResultMatchers.model().attribute("courses", Matchers.hasSize(2)))
				.andExpect(MockMvcResultMatchers.model().attribute("courses", Matchers.contains(course1, course2)));
	}

	@Test
	@WithMockUser(roles = { "ADMIN", "STUDENT", "TEACHER", "STAFF" })
	void testSearchCourse_WhenSearchTypeIsStudent() throws Exception {
		int studentId = 1;
		Course course1 = new Course("Course X");
		Course course2 = new Course("Course Y");
		List<Course> courses = Arrays.asList(course1, course2);

		when(courseService.findCoursesRelatedToStudent(studentId)).thenReturn(courses);

		mockMvc.perform(MockMvcRequestBuilders.get("/course/search-result").param("searchType", "student")
				.param("studentId", String.valueOf(studentId))).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("course/course-list"))
				.andExpect(MockMvcResultMatchers.model().attributeExists("courses"))
				.andExpect(MockMvcResultMatchers.model().attribute("courses", Matchers.hasSize(2)))
				.andExpect(MockMvcResultMatchers.model().attribute("courses", Matchers.contains(course1, course2)));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void testOpenCourseCard_WhenCourseExists() throws Exception {
		Course course = new Course("History of Magic");
		course.setId(1);

		when(courseService.findCourseById(course.getId())).thenReturn(Optional.of(course));

		mockMvc.perform(MockMvcRequestBuilders.get("/course/course-card/{courseId}", course.getId()))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("course/course-card"))
				.andExpect(MockMvcResultMatchers.model().attributeExists("course"))
				.andExpect(MockMvcResultMatchers.model().attribute("course", Matchers.sameInstance(course)));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void testOpenCourseCard_WhenCourseDoesNotExist() throws Exception {
		int courseId = 999;

		when(courseService.findCourseById(courseId)).thenReturn(Optional.empty());

		mockMvc.perform(MockMvcRequestBuilders.get("/course/course-card/{courseId}", courseId))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.redirectedUrl("/course/course-list"));
	}
}
