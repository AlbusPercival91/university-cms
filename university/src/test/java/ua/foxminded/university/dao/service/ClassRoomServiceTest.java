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
import ua.foxminded.university.dao.entities.ClassRoom;
import ua.foxminded.university.dao.interfaces.ClassRoomRepository;
import ua.foxminded.university.dao.validation.UniqueEmailValidator;

@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
		ClassRoomService.class, UniqueEmailValidator.class }))
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test-container")
@Sql(scripts = { "/drop_data.sql", "/init_tables.sql",
		"/insert_test_data.sql", }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ClassRoomServiceTest {

	@Autowired
	private ClassRoomRepository classRoomRepository;

	@Autowired
	private ClassRoomService classRoomService;

	@ParameterizedTest
	@CsvSource({ "1", "2", "3" })
	void testDeleteClassRoomById_ShouldReturnClassRoomId(int classRoomId) {
		Assertions.assertEquals(classRoomId, classRoomRepository.findById(classRoomId).get().getId());
		Assertions.assertEquals(classRoomId, classRoomService.deleteClassRoomById(classRoomId));
	}

	@Test
	void testDeleteClassRoomById_WhenIdNotFound_ShouldThrowNoSuchElementException() {
		Exception noSuchElementException = assertThrows(Exception.class, () -> classRoomService.deleteClassRoomById(4));
		Assertions.assertEquals("Class room not found", noSuchElementException.getMessage());
	}

	@ParameterizedTest
	@CsvSource({ "1", "2", "3" })
	void testUpdateClassRoomById_ShouldReturnUpdatedClassRoom(int classRoomId) {
		ClassRoom expectedClassRoom = new ClassRoom("Privet drive", 4, 1);
		expectedClassRoom.setId(classRoomId);

		Assertions.assertEquals(expectedClassRoom,
				classRoomService.updateClassRoomById(classRoomId, expectedClassRoom));
	}

	@Test
	void testUpdateClassRoomById_WhenIdNotFound_ShouldThrowNoSuchElementException() {
		ClassRoom expectedClassRoom = new ClassRoom("Privet drive", 4, 1);
		expectedClassRoom.setId(1);

		Exception noSuchElementException = assertThrows(Exception.class,
				() -> classRoomService.updateClassRoomById(4, expectedClassRoom));
		Assertions.assertEquals("Class room not found", noSuchElementException.getMessage());
	}
}
