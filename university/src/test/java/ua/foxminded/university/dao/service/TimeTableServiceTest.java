package ua.foxminded.university.dao.service;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ua.foxminded.university.dao.entities.ClassRoom;
import ua.foxminded.university.dao.entities.Course;
import ua.foxminded.university.dao.entities.Department;
import ua.foxminded.university.dao.entities.Faculty;
import ua.foxminded.university.dao.entities.Group;
import ua.foxminded.university.dao.entities.Teacher;

@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
		TimeTableService.class }))
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test-container")
@Sql(scripts = { "/drop_data.sql", "/init_tables.sql",
		"/insertTestData.sql" }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class TimeTableServiceTest {

	@Autowired
	private TimeTableService timeTableService;

	@Test
	void testGetTeacherTimeTables_ShouldReturnTeacherTimeTable() {
		Faculty science = new Faculty("Faculty of Science");
		science.setId(1);
		Department math = new Department("Mathematics Department", science);
		math.setId(1);
		Course mathematics = new Course("Mathematics", "Advanced mathematics course");
		mathematics.setId(1);
		Teacher teacher = new Teacher("Jennifer", "Smith", true, "jennifer.smith@example.com", "password10", math,
				mathematics);
		teacher.setId(1);
		Group a = new Group("Group A", science);
		a.setId(1);
		ClassRoom room = new ClassRoom("Main Street", 1, 101);
		room.setId(1);
		// Create a sample time table for the teacher
		LocalDateTime timeStart = LocalDateTime.now();
		LocalDateTime timeEnd = timeStart.plusHours(2);

		timeTableService.createTeacherTimeTable(timeStart, timeEnd, teacher, a, room);
		System.out.println(timeTableService.getTeacherTimeTables(teacher));
		Assertions.assertEquals("Hello", timeTableService.getTeacherTimeTables(teacher).toString());
	}
}
