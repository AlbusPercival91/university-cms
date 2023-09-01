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
import ua.foxminded.university.dao.entities.Staff;
import ua.foxminded.university.dao.interfaces.StaffRepository;
import ua.foxminded.university.security.UserRole;
import ua.foxminded.university.validation.UniqueEmailValidator;

@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = { StaffService.class,
		UniqueEmailValidator.class }))
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test-container")
@Sql(scripts = { "/drop_data.sql", "/init_tables.sql",
		"/insert_test_data.sql", }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class StaffServiceTest {

	@Autowired
	private StaffRepository staffRepository;

	@Autowired
	private StaffService staffService;

	@ParameterizedTest
	@CsvSource({ "1", "2", "3" })
	void testDeleteStaffById_ShouldReturnStaffId(int staffId) {
		Assertions.assertEquals(staffId, staffRepository.findById(staffId).get().getId());
		Assertions.assertEquals(staffId, staffService.deleteStaffById(staffId));
	}

	@Test
	void testDeleteStaffById_WhenIdNotFound_ShouldThrowNoSuchElementException() {
		Exception noSuchElementException = assertThrows(Exception.class, () -> staffService.deleteStaffById(4));
		Assertions.assertEquals("Staff not found", noSuchElementException.getMessage());
	}

	@ParameterizedTest
	@CsvSource({ "1", "2", "3" })
	void testUpdateStaffById_ShouldReturnUpdatedStaff(int staffId) {
		Staff expectedStaff = new Staff("Argus", "Filch", false, "filch@fakemail.com", "1234", "supply manager",
				"responsiblle for school inventory, repairing, new supplies");
		expectedStaff.setId(staffId);
		expectedStaff.setRole(UserRole.STAFF);

		Assertions.assertEquals(expectedStaff, staffService.updateStaffById(staffId, expectedStaff));
	}

	@Test
	void testUpdateStaffById_WhenIdNotFound_ShouldThrowNoSuchElementException() {
		Staff expectedStaff = new Staff("Argus", "Filch", false, "filch@fakemail.com", "1234", "supply manager",
				"responsiblle for school inventory, repairing, new supplies");
		expectedStaff.setId(1);

		Exception noSuchElementException = assertThrows(Exception.class,
				() -> staffService.updateStaffById(4, expectedStaff));
		Assertions.assertEquals("Staff not found", noSuchElementException.getMessage());
	}
}
