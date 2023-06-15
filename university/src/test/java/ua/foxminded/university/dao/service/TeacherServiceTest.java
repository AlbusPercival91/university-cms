package ua.foxminded.university.dao.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
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
import ua.foxminded.university.dao.entities.Teacher;
import ua.foxminded.university.dao.interfaces.TeacherRepository;

@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
		TeacherService.class }))
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test-container")
@Sql(scripts = { "/drop_data.sql", "/init_tables.sql",
		"/insert_test_data.sql", }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class TeacherServiceTest {

	@Autowired
	private TeacherRepository teacherRepository;

	@Autowired
	private TeacherService teacherService;

	@ParameterizedTest
	@CsvSource({ "1", "2", "3" })
	void testDeleteTeacherById_ShouldReturnTeacherId(int teacherId) {
		Assertions.assertEquals(teacherId, teacherRepository.findById(teacherId).get().getId());
		Assertions.assertEquals(teacherId, teacherService.deleteTeacherById(teacherId));
	}

	@Test
	void testDeleteTeacherById_WhenIdNotFound_ShouldThrowNoSuchElementException() {
		Exception noSuchElementException = assertThrows(Exception.class, () -> teacherService.deleteTeacherById(4));
		Assertions.assertEquals("Teacher not found", noSuchElementException.getMessage());
	}

	@ParameterizedTest
	@CsvSource({ "1", "2", "3" })
	void testUpdateTeacherById_ShouldReturnUpdatedTeacher(int teacherId) {
		Teacher expectedTeacher = new Teacher("Severus", "Snape", false, "snape@fakemail.com", "1234", null, null);
		expectedTeacher.setId(teacherId);

		Assertions.assertEquals(expectedTeacher, teacherService.updateTeacherById(teacherId, expectedTeacher));
	}

	@Test
	void testUpdateTeacherById_WhenIdNotFound_ShouldThrowNoSuchElementException() {
		Teacher expectedTeacher = new Teacher("Severus", "Snape", false, "snape@fakemail.com", "1234", null, null);
		expectedTeacher.setId(1);

		Exception noSuchElementException = assertThrows(Exception.class,
				() -> teacherService.updateTeacherById(4, expectedTeacher));
		Assertions.assertEquals("Teacher not found", noSuchElementException.getMessage());
	}
}
