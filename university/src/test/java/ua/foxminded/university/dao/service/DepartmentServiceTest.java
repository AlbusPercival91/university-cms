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
import ua.foxminded.university.dao.entities.Department;
import ua.foxminded.university.dao.interfaces.DepartmentRepository;
import ua.foxminded.university.validation.Message;
import ua.foxminded.university.validation.UniqueEmailValidator;

@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
        DepartmentService.class, UniqueEmailValidator.class }))
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test-container")
@Sql(scripts = { "/drop_data.sql", "/init_tables.sql",
        "/insert_test_data.sql", }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class DepartmentServiceTest {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private DepartmentService departmentService;

    @ParameterizedTest
    @CsvSource({ "1", "2", "3" })
    void testDeleteDepartmentById_ShouldReturnDepartmentId(int departmentId) {
        Assertions.assertEquals(departmentId, departmentRepository.findById(departmentId).get().getId());
        Assertions.assertEquals(departmentId, departmentService.deleteDepartmentById(departmentId));
    }

    @Test
    void testDeleteDepartmentById_WhenIdNotFound_ShouldThrowNoSuchElementException() {
        Exception noSuchElementException = assertThrows(Exception.class,
                () -> departmentService.deleteDepartmentById(4));
        Assertions.assertEquals(Message.DEPARTMENT_NOT_FOUND, noSuchElementException.getMessage());
    }

    @ParameterizedTest
    @CsvSource({ "1", "2", "3" })
    void testUpdateDepartmentById_ShouldReturnUpdatedDepartment(int departmentId) {
        Optional<Department> department = departmentRepository.findById(departmentId);

        Department updatedDapertment = new Department("Department of Medicine", department.get().getFaculty());
        updatedDapertment.setId(departmentId);

        Assertions.assertEquals(updatedDapertment,
                departmentService.updateDepartmentById(departmentId, updatedDapertment));
    }

    @Test
    void testUpdateDepartmentById_WhenIdNotFound_ShouldThrowNoSuchElementException() {
        Department expectedDepartment = new Department("Education Department", null);
        expectedDepartment.setId(1);

        Exception noSuchElementException = assertThrows(Exception.class,
                () -> departmentService.updateDepartmentById(4, expectedDepartment));
        Assertions.assertEquals(Message.DEPARTMENT_NOT_FOUND, noSuchElementException.getMessage());
    }
}
