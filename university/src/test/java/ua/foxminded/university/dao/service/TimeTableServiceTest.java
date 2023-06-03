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
import ua.foxminded.university.dao.entities.TimeTable;
import ua.foxminded.university.dao.interfaces.TimeTableRepository;

@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
		TimeTableService.class }))
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test-container")
@Sql(scripts = { "/drop_data.sql", "/init_tables.sql",
		"/insert_test_data.sql" }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class TimeTableServiceTest {

	@Autowired
	private TimeTableService timeTableService;

	@Autowired
	private TimeTableRepository timeTableRepository;

	@Test
	void testGetTeacherTimeTable() {
		Faculty scienceFaculty = new Faculty("Faculty of Science");
		scienceFaculty.setId(1);
		Department mathematicsDepartment = new Department("Mathematics Department", scienceFaculty);
		mathematicsDepartment.setId(1);
		Course mathCourse = new Course("Mathematics", "Advanced mathematics course");
		mathCourse.setId(1);
		Teacher teacher = new Teacher("Jennifer", "Smith", true, "jennifer.smith@example.com", "password10",
				mathematicsDepartment, mathCourse);
		teacher.setId(1);
		Group a = new Group("Group A", scienceFaculty);
		a.setId(1);
		ClassRoom room = new ClassRoom("Main Street", 1, 101);
		room.setId(1);
		LocalDateTime timeStart = LocalDateTime.now();
		LocalDateTime timeEnd = timeStart.plusHours(2);

		TimeTable expected = new TimeTable(timeStart, timeEnd, teacher, a, room);
		timeTableRepository.save(expected);
		Assertions.assertSame(expected, timeTableService.getTeacherTimeTable(teacher));
	}

	@Test
	void testGetGroupTimeTable() {
		Faculty scienceFaculty = new Faculty("Faculty of Science");
		scienceFaculty.setId(1);
		Department mathematicsDepartment = new Department("Mathematics Department", scienceFaculty);
		mathematicsDepartment.setId(1);
		Course mathCourse = new Course("Mathematics", "Advanced mathematics course");
		mathCourse.setId(1);
		Teacher teacher = new Teacher("James", "Wilson", true, "james.wilson@example.com", "password11",
				mathematicsDepartment, mathCourse);
		teacher.setId(1);
		Group a = new Group("Group A", scienceFaculty);
		a.setId(1);
		ClassRoom room = new ClassRoom("Main Street", 1, 101);
		room.setId(1);
		LocalDateTime timeStart = LocalDateTime.now();
		LocalDateTime timeEnd = timeStart.plusHours(2);

		TimeTable expected = new TimeTable(timeStart, timeEnd, a, teacher, mathCourse, room);
		timeTableRepository.save(expected);

		Assertions.assertEquals(expected, timeTableService.getGroupTimeTable(a));
	}
}
