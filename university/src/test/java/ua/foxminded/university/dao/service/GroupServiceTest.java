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
import ua.foxminded.university.dao.entities.Group;
import ua.foxminded.university.dao.interfaces.GroupRepository;

@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
		GroupService.class }))
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test-container")
@Sql(scripts = { "/drop_data.sql", "/init_tables.sql",
		"/insert_test_data.sql", }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class GroupServiceTest {

	@Autowired
	private GroupRepository groupRepository;

	@Autowired
	private GroupService groupService;

	@ParameterizedTest
	@CsvSource({ "1", "2", "3" })
	void testDeleteGroupById_ShouldReturnGrouptId(int groupId) {
		Assertions.assertEquals(groupId, groupRepository.findById(groupId).get().getId());
		Assertions.assertEquals(groupId, groupService.deleteGroupById(groupId));
	}

	@Test
	void testDeleteGroupById_WhenIdNotFound_ShouldThrowNoSuchElementException() {
		Exception noSuchElementException = assertThrows(Exception.class, () -> groupService.deleteGroupById(4));
		Assertions.assertEquals("Group not found", noSuchElementException.getMessage());
	}

	@ParameterizedTest
	@CsvSource({ "1", "2", "3" })
	void testUpdateGroupById_ShouldReturnUpdatedGroup(int groupId) {
		Group expectedGroup = new Group("fake-01", null);
		expectedGroup.setId(groupId);

		Assertions.assertEquals(expectedGroup, groupService.updateGroupById(groupId, expectedGroup));
	}

	@Test
	void testUpdateGroupById_WhenIdNotFound_ShouldThrowNoSuchElementException() {
		Group expectedGroup = new Group("fake-01", null);
		expectedGroup.setId(1);

		Exception noSuchElementException = assertThrows(Exception.class,
				() -> groupService.updateGroupById(4, expectedGroup));
		Assertions.assertEquals("Group not found", noSuchElementException.getMessage());
	}
}
