package ua.foxminded.university.dao.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ua.foxminded.university.dao.entities.Course;
import ua.foxminded.university.dao.entities.Student;
import ua.foxminded.university.dao.entities.Teacher;
import ua.foxminded.university.dao.interfaces.CourseRepository;
import ua.foxminded.university.dao.interfaces.StudentRepository;
import ua.foxminded.university.dao.interfaces.TeacherRepository;

@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = { CourseService.class,
		TeacherService.class, StudentService.class , GroupService.class}))
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test-container")
@Sql(scripts = { "/drop_data.sql", "/init_tables.sql",
		"/insert_test_data.sql", }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CourseServiceTest {

	@Autowired
	private CourseRepository courseRepository;

	@Autowired
	private CourseService courseService;

	@Autowired
	private TeacherService teacherService;

	@Autowired
	private TeacherRepository teacherRepository;

	@Autowired
	private StudentService studentService;

	@Autowired
	private StudentRepository studentRepository;

	@ParameterizedTest
	@CsvSource({ "1", "2", "3" })
	void testDeleteCourseById_ShouldReturnCourseId(int courseId) {
		Assertions.assertEquals(courseId, courseRepository.findById(courseId).get().getId());
		Assertions.assertEquals(courseId, courseService.deleteCourseById(courseId));
	}

	@Test
	void testDeleteCourseById_WhenIdNotFound_ShouldThrowNoSuchElementException() {
		Exception noSuchElementException = assertThrows(Exception.class, () -> courseService.deleteCourseById(4));
		Assertions.assertEquals("Course not found", noSuchElementException.getMessage());
	}

	@ParameterizedTest
	@CsvSource({ "1", "2", "3" })
	void testUpdateCourseById_ShouldReturnUpdatedCourse(int courseId) {
		Course expectedCourse = new Course("History of Magic");
		expectedCourse.setId(courseId);

		Assertions.assertEquals(expectedCourse, courseService.updateCourseById(courseId, expectedCourse));
	}

	@Test
	void testUpdateCourseById_WhenIdNotFound_ShouldThrowNoSuchElementException() {
		Course expectedCourse = new Course("History of Magic");
		expectedCourse.setId(1);

		Exception noSuchElementException = assertThrows(Exception.class,
				() -> courseService.updateCourseById(4, expectedCourse));
		Assertions.assertEquals("Course not found", noSuchElementException.getMessage());
	}

	@ParameterizedTest
	@CsvSource({ "3, 2, 3", "2, 1, 3", "1, 1, 2" })
	void testFindCoursesRelatedToTeacher_ShouldReturnListCoursesRelatedToTeacherByTeacherId(int teacherId,
			int firstCourseId, int secondCourseId) {
		Optional<Teacher> teacher = teacherRepository.findById(teacherId);
		Optional<Course> firstCourse = courseRepository.findById(firstCourseId);
		Optional<Course> secondCourse = courseRepository.findById(secondCourseId);
		List<Course> expectedList = new ArrayList<>() {
			private static final long serialVersionUID = 1L;

			{
				add(firstCourse.get());
				add(secondCourse.get());
			}
		};
		teacherService.addTeacherToTheCourse(teacher.get().getId(), firstCourse.get().getCourseName());
		teacherService.addTeacherToTheCourse(teacher.get().getId(), secondCourse.get().getCourseName());

		Assertions.assertEquals(expectedList, courseService.findCoursesRelatedToTeacher(teacher.get().getId()));
	}

	@ParameterizedTest
	@CsvSource({ "3, 2, 3", "2, 1, 3", "1, 1, 2" })
	void testFindCoursesRelatedToStudent_ShouldReturnListCoursesRelatedToStudentByStudentId(int studentId,
			int firstCourseId, int secondCourseId) {
		Optional<Student> student = studentRepository.findById(studentId);
		Optional<Course> firstCourse = courseRepository.findById(firstCourseId);
		Optional<Course> secondCourse = courseRepository.findById(secondCourseId);
		List<Course> expectedList = new ArrayList<>() {
			private static final long serialVersionUID = 1L;

			{
				add(firstCourse.get());
				add(secondCourse.get());
			}
		};
		studentService.addStudentToTheCourse(student.get().getId(), firstCourse.get().getCourseName());
		studentService.addStudentToTheCourse(student.get().getId(), secondCourse.get().getCourseName());

		Assertions.assertEquals(expectedList, courseService.findCoursesRelatedToStudent(student.get().getId()));
	}
}
