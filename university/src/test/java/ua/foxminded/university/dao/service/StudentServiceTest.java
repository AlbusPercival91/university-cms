package ua.foxminded.university.dao.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
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
import ua.foxminded.university.dao.interfaces.CourseRepository;
import ua.foxminded.university.dao.interfaces.StudentRepository;

@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
		StudentService.class }))
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test-container")
@Sql(scripts = { "/drop_data.sql", "/init_tables.sql",
		"/insert_test_data.sql", }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class StudentServiceTest {

	@Autowired
	private StudentService studentService;

	@Autowired
	private StudentRepository studentRepository;

	@Autowired
	private CourseRepository courseRepository;

	@ParameterizedTest
	@CsvSource({ "1", "2", "3" })
	void testDeleteStudentById_ShouldReturnStudentId(int studentId) {
		Assertions.assertEquals(studentId, studentRepository.findById(studentId).get().getId());
		Assertions.assertEquals(studentId, studentService.deleteStudentById(studentId));
	}

	@Test
	void testDeleteStudentById_WhenIdNotFound_ShouldThrowNoSuchElementException() {
		Exception noSuchElementException = assertThrows(Exception.class, () -> studentService.deleteStudentById(4));
		Assertions.assertEquals("Student not found", noSuchElementException.getMessage());
	}

	@ParameterizedTest
	@CsvSource({ "1", "2", "3" })
	void testUpdateStudentById_ShouldReturnUpdatedStudent(int studentId) {
		Student expectedStudent = new Student("John", "Connor", false, "johnconnor@fakemail.com", "1234", null);
		expectedStudent.setId(studentId);

		Assertions.assertEquals(expectedStudent, studentService.updateStudentById(studentId, expectedStudent));
	}

	@Test
	void testUpdateStudentById_WhenIdNotFound_ShouldThrowNoSuchElementException() {
		Student expectedStudent = new Student("John", "Connor", false, "johnconnor@fakemail.com", "1234", null);
		expectedStudent.setId(1);

		Exception noSuchElementException = assertThrows(Exception.class,
				() -> studentService.updateStudentById(4, expectedStudent));
		Assertions.assertEquals("Student not found", noSuchElementException.getMessage());
	}

	@ParameterizedTest
	@CsvSource({ "1, Chemistry", "2, Chemistry", "3, Physics", "2, Physics" })
	void testFindStudentsRelatedToCourse_ShouldReturnListOfStudentsAddedToCourse(int studentId, String courseName) {
		int studentsAdded = studentService.addStudentToTheCourse(studentId, courseName);
		Assertions.assertEquals(1, studentsAdded);
		Assertions.assertEquals(1, studentService.findStudentsRelatedToCourse(courseName).size());

		int studentsRemoved = studentService.removeStudentFromCourse(studentId, courseName);
		Assertions.assertEquals(1, studentsRemoved);
		Assertions.assertTrue(studentService.findStudentsRelatedToCourse(courseName).isEmpty());
	}

	@Test
	void testRemoveStudentFromCourse_WhenStudentIsNotRelatedToCourse_ShouldThrowIllegalStateException() {
		Optional<Course> course = courseRepository.findById(1);

		Exception illegalStateException = assertThrows(Exception.class,
				() -> studentService.removeStudentFromCourse(1, course.get().getCourseName()));
		Assertions.assertEquals("Student is not related with this Course!", illegalStateException.getMessage());
	}

}
