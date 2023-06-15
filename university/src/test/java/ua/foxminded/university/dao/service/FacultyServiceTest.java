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
import ua.foxminded.university.dao.entities.Faculty;
import ua.foxminded.university.dao.interfaces.FacultyRepository;

@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
		FacultyService.class }))
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test-container")
@Sql(scripts = { "/drop_data.sql", "/init_tables.sql",
		"/insert_test_data.sql", }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class FacultyServiceTest {

	@Autowired
	private FacultyRepository facultyRepository;

	@Autowired
	private FacultyService facultyService;

	@ParameterizedTest
	@CsvSource({ "1", "2", "3" })
	void testDeleteFacultyById_ShouldReturnFacultytId(int facultyId) {
		Assertions.assertEquals(facultyId, facultyRepository.findById(facultyId).get().getId());
		Assertions.assertEquals(facultyId, facultyService.deleteFacultyById(facultyId));
	}

	@Test
	void testDeleteFacultyById_WhenIdNotFound_ShouldThrowNoSuchElementException() {
		Exception noSuchElementException = assertThrows(Exception.class, () -> facultyService.deleteFacultyById(4));
		Assertions.assertEquals("Faculty not found", noSuchElementException.getMessage());
	}

	@ParameterizedTest
	@CsvSource({ "1", "2", "3" })
	void testUpdateFacultyById_ShouldReturnUpdatedFaculty(int facultyId) {
		Faculty expectedFaculty = new Faculty("Griffindor");
		expectedFaculty.setId(facultyId);

		Assertions.assertEquals(expectedFaculty, facultyService.updateFacultyById(facultyId, expectedFaculty));
	}

	@Test
	void testUpdateFacultyById_WhenIdNotFound_ShouldThrowNoSuchElementException() {
		Faculty expectedFaculty = new Faculty("Griffindor");
		expectedFaculty.setId(1);

		Exception noSuchElementException = assertThrows(Exception.class,
				() -> facultyService.updateFacultyById(4, expectedFaculty));
		Assertions.assertEquals("Faculty not found", noSuchElementException.getMessage());
	}
}
