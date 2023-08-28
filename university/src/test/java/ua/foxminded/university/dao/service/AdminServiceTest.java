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
import ua.foxminded.university.dao.entities.Admin;
import ua.foxminded.university.dao.interfaces.AdminRepository;
import ua.foxminded.university.security.UserRole;
import ua.foxminded.university.validation.UniqueEmailValidator;

@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = { AdminService.class,
		UniqueEmailValidator.class }))
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test-container")
@Sql(scripts = { "/drop_data.sql", "/init_tables.sql",
		"/insert_test_data.sql", }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class AdminServiceTest {

	@Autowired
	private AdminRepository adminRepository;

	@Autowired
	private AdminService adminService;

	@ParameterizedTest
	@CsvSource({ "1", "2", "3" })
	void testDeleteAdminById_ShouldReturnAdminId(int adminId) {
		Assertions.assertEquals(adminId, adminRepository.findById(adminId).get().getId());
		Assertions.assertEquals(adminId, adminService.deleteAdminById(adminId));
	}

	@Test
	void testDeleteAdminById_WhenIdNotFound_ShouldThrowNoSuchElementException() {
		Exception noSuchElementException = assertThrows(Exception.class, () -> adminService.deleteAdminById(4));
		Assertions.assertEquals("Admin not found", noSuchElementException.getMessage());
	}

	@ParameterizedTest
	@CsvSource({ "1", "2", "3" })
	void testUpdateAdminById_ShouldReturnUpdatedAdmin(int adminId) {
		Admin expectedAdmin = new Admin("Albus", "Dumbledore", false, "albus@fakemail.com", "1234");
		expectedAdmin.setId(adminId);
		expectedAdmin.setRole(UserRole.ADMIN);

		Assertions.assertEquals(expectedAdmin, adminService.updateAdminById(adminId, expectedAdmin));
	}

	@Test
	void testUpdateAdminById_WhenIdNotFound_ShouldThrowNoSuchElementException() {
		Admin expectedAdmin = new Admin("Albus", "Dumbledore", false, "albus@fakemail.com", "1234");
		expectedAdmin.setId(1);

		Exception noSuchElementException = assertThrows(Exception.class,
				() -> adminService.updateAdminById(4, expectedAdmin));
		Assertions.assertEquals("Admin not found", noSuchElementException.getMessage());
	}
}
