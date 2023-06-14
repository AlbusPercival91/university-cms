package ua.foxminded.university.dao.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ua.foxminded.university.dao.entities.Student;
import ua.foxminded.university.dao.entities.TimeTable;
import ua.foxminded.university.dao.interfaces.StudentRepository;
import ua.foxminded.university.dao.interfaces.TimeTableRepository;

@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
		TimeTableService.class, TimeTableBuilder.class }))
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test-container")
@Sql(scripts = { "/drop_data.sql", "/init_tables.sql",
		"/insert_test_data.sql", }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class TimeTableServiceTest {

	@Autowired
	private TimeTableService timeTableService;

	@Autowired
	private TimeTableRepository timeTableRepository;

	@Autowired
	private TimeTableBuilder timeTableBuilder;

	@Autowired
	private StudentRepository studentRepository;

	@ParameterizedTest
	@CsvSource({ "2023-09-01, 09:00, 10:30, 1, 1, 1, 1", "2023-09-01, 12:00, 13:30, 2, 2, 2, 2",
			"2023-09-02, 09:00, 10:30, 3, 3, 3, 3", "2023-09-02, 12:00, 13:30, 1, 1, 2, 3" })
	void testCreateTimeTable_ShouldSaveTimeTableToDatabase(LocalDate date, LocalTime timeFrom, LocalTime timeTo,
			int teacherId, int courseId, int groupId, int classRoomId) {
		TimeTable timeTable = timeTableBuilder.saveTimeTable(date, timeFrom, timeTo, teacherId, courseId, groupId,
				classRoomId);

		Assertions.assertEquals(timeTableRepository.findById(1).get(), timeTable);
	}

	@ParameterizedTest
	@CsvSource({ "2023-09-01, 09:00, 10:30, 1, 1, 1, 1 ", "2023-09-01, 12:00, 13:30, 2, 2, 2, 2",
			"2023-09-02, 09:00, 10:30, 3, 3, 3, 3", "2023-09-02, 12:00, 13:30, 1, 1, 2, 3" })
	void testGetTeacherTimeTable_ShouldReturnTeacherTimeTable(LocalDate date, LocalTime timeFrom, LocalTime timeTo,
			int teacherId, int courseId, int groupId, int classRoomId) {
		List<TimeTable> expectedTimeTableList = new ArrayList<>();
		TimeTable timeTable = timeTableBuilder.saveTimeTable(date, timeFrom, timeTo, teacherId, courseId, groupId,
				classRoomId);
		expectedTimeTableList.add(timeTable);

		Assertions.assertEquals(expectedTimeTableList, timeTableService.getTeacherTimeTable(timeTable.getTeacher()));
	}

	@ParameterizedTest
	@CsvSource({ "2023-09-01, 09:00, 10:30, 1, 1, 1, 1, 1", "2023-09-01, 12:00, 13:30, 2, 2, 2, 2, 2",
			"2023-09-02, 09:00, 10:30, 3, 3, 3, 3, 3", "2023-09-02, 12:00, 13:30, 1, 1, 3, 1, 3" })
	void testGetStudentTimeTable_ShouldReturnSameTimeTableForStudentAndHisGroup(LocalDate date, LocalTime timeFrom,
			LocalTime timeTo, int teacherId, int courseId, int groupId, int classRoomId, int studentId) {
		Optional<Student> student = studentRepository.findById(studentId);
		TimeTable timeTable = timeTableBuilder.saveTimeTable(date, timeFrom, timeTo, teacherId, courseId, groupId,
				classRoomId);

		Assertions.assertEquals(timeTableService.getGroupTimeTable(timeTable.getGroup()),
				timeTableService.getStudentTimeTable(student.get()));
	}

	@ParameterizedTest
	@CsvSource({ "2023-09-01, 09:00, 10:00, 1, 1, 1, 1" })
	void testGetTeacherTimeTableByDate_ShouldReturnTeacherTimeTable(LocalDate date, LocalTime timeFrom,
			LocalTime timeTo, int teacherId, int courseId, int groupId, int classRoomId) {
		TimeTable timeTable = new TimeTable();
		List<TimeTable> expectedTimeTableList = new ArrayList<>();

		for (int i = 0; i < 3; i++) {
			timeTable = timeTableBuilder.saveTimeTable(date, timeFrom.plusHours(i), timeTo.plusHours(i), teacherId,
					courseId, groupId++, classRoomId++);
			expectedTimeTableList.add(timeTable);
		}

		Assertions.assertEquals(expectedTimeTableList,
				timeTableService.getTeacherTimeTableByDate(date, date, timeTable.getTeacher()));
	}

	@ParameterizedTest
	@CsvSource({ "2023-09-01, 09:00, 10:00, 1, 1, 1, 1, 1" })
	void testGetStudentTimeTableByDate_ShouldReturnSameTimeTableForStudentAndHisGroup(LocalDate date,
			LocalTime timeFrom, LocalTime timeTo, int teacherId, int courseId, int groupId, int classRoomId,
			int studentId) {
		TimeTable timeTable = new TimeTable();
		Optional<Student> student = studentRepository.findById(studentId);

		for (int i = 0; i < 3; i++) {
			timeTable = timeTableBuilder.saveTimeTable(date, timeFrom.plusHours(i), timeTo.plusHours(i), teacherId++,
					courseId++, groupId, classRoomId++);
		}

		Assertions.assertEquals(timeTableService.getGroupTimeTableByDate(date, date, timeTable.getGroup()),
				timeTableService.getStudentTimeTableByDate(date, date, student.get()));
	}

	@ParameterizedTest
	@CsvSource({ "2023-09-01, 09:00, 10:30, 1, 1, 1, 1 " })
	void testGetAllTimeTablesByDate_ShouldReturnAllTimeTablesBetweenDates(LocalDate date, LocalTime timeFrom,
			LocalTime timeTo, int teacherId, int courseId, int groupId, int classRoomId) {
		TimeTable timeTable = new TimeTable();
		List<TimeTable> expectedTimeTableList = new ArrayList<>();

		for (int i = 0; i < 3; i++) {
			timeTable = timeTableBuilder.saveTimeTable(date.plusDays(i), timeFrom, timeTo, teacherId++, courseId++,
					groupId++, classRoomId++);
			expectedTimeTableList.add(timeTable);
		}

		Assertions.assertEquals(expectedTimeTableList, timeTableService.getAllTimeTablesByDate(date, date.plusDays(2)));
	}

}