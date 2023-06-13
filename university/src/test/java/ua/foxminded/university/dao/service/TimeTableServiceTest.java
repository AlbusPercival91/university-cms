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
import ua.foxminded.university.dao.entities.ClassRoom;
import ua.foxminded.university.dao.entities.Course;
import ua.foxminded.university.dao.entities.Group;
import ua.foxminded.university.dao.entities.Student;
import ua.foxminded.university.dao.entities.Teacher;
import ua.foxminded.university.dao.entities.TimeTable;
import ua.foxminded.university.dao.interfaces.ClassRoomRepository;
import ua.foxminded.university.dao.interfaces.CourseRepository;
import ua.foxminded.university.dao.interfaces.GroupRepository;
import ua.foxminded.university.dao.interfaces.StudentRepository;
import ua.foxminded.university.dao.interfaces.TeacherRepository;
import ua.foxminded.university.dao.interfaces.TimeTableRepository;

@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
		TimeTableService.class }))
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
	private CourseRepository courseRepository;

	@Autowired
	private TeacherRepository teacherRepository;

	@Autowired
	private StudentRepository studentRepository;

	@Autowired
	private GroupRepository groupRepository;

	@Autowired
	private ClassRoomRepository classRoomRepository;

	@ParameterizedTest
	@CsvSource({ "2023-09-01, 09:00, 10:30, 1, 1, 1, 1", "2023-09-01, 12:00, 13:30, 2, 2, 2, 2",
			"2023-09-02, 09:00, 10:30, 3, 3, 3, 3", "2023-09-02, 12:00, 13:30, 1, 1, 2, 3" })
	void testCreateTimeTable_ShouldSaveTimeTableToDatabase(LocalDate date, LocalTime timeFrom, LocalTime timeTo,
			int teacherId, int courseId, int groupId, int classRoomId) {
		Optional<Teacher> teacher = teacherRepository.findById(teacherId);
		Optional<Course> course = courseRepository.findById(courseId);
		Optional<Group> group = groupRepository.findById(groupId);
		Optional<ClassRoom> classRoom = classRoomRepository.findById(classRoomId);
		TimeTable expectedTimeTable = new TimeTable(date, timeFrom, timeTo, teacher.get(), course.get(), group.get(),
				classRoom.get());
		expectedTimeTable.setId(1);

		Assertions.assertEquals(expectedTimeTable, timeTableService.createTimeTable(date, timeFrom, timeTo,
				teacher.get(), course.get(), group.get(), classRoom.get()));
	}

	@ParameterizedTest
	@CsvSource({ "2023-09-01, 09:00, 10:30, 1, 1, 1, 1 ", "2023-09-01, 12:00, 13:30, 2, 2, 2, 2",
			"2023-09-02, 09:00, 10:30, 3, 3, 3, 3", "2023-09-02, 12:00, 13:30, 1, 1, 2, 3" })
	void testGetTeacherTimeTable_ShouldReturnTeacherTimeTable(LocalDate date, LocalTime timeFrom, LocalTime timeTo,
			int teacherId, int courseId, int groupId, int classRoomId) {
		Optional<Teacher> teacher = teacherRepository.findById(teacherId);
		Optional<Course> course = courseRepository.findById(courseId);
		Optional<Group> group = groupRepository.findById(groupId);
		Optional<ClassRoom> classRoom = classRoomRepository.findById(classRoomId);
		TimeTable expectedTimeTable = new TimeTable(date, timeFrom, timeTo, teacher.get(), course.get(), group.get(),
				classRoom.get());
		expectedTimeTable.setId(1);
		List<TimeTable> expectedTimeTableList = new ArrayList<TimeTable>();
		expectedTimeTableList.add(expectedTimeTable);
		timeTableRepository.save(expectedTimeTable);

		Assertions.assertEquals(expectedTimeTableList, timeTableService.getTeacherTimeTable(teacher.get()));
	}

	@ParameterizedTest
	@CsvSource({ "2023-09-01, 09:00, 10:30, 1, 1, 1, 1, 1", "2023-09-01, 12:00, 13:30, 2, 2, 2, 2, 2",
			"2023-09-02, 09:00, 10:30, 3, 3, 3, 3, 3", "2023-09-02, 12:00, 13:30, 1, 1, 3, 1, 3" })
	void testGetStudentTimeTable_ShouldReturnStudentTimeTable(LocalDate date, LocalTime timeFrom, LocalTime timeTo,
			int teacherId, int courseId, int groupId, int classRoomId, int studentId) {
		Optional<Teacher> teacher = teacherRepository.findById(teacherId);
		Optional<Course> course = courseRepository.findById(courseId);
		Optional<Group> group = groupRepository.findById(groupId);
		Optional<ClassRoom> classRoom = classRoomRepository.findById(classRoomId);
		Optional<Student> student = studentRepository.findById(studentId);
		TimeTable expectedTimeTable = new TimeTable(date, timeFrom, timeTo, teacher.get(), course.get(), group.get(),
				classRoom.get());
		expectedTimeTable.setId(1);
		timeTableRepository.save(expectedTimeTable);

		Assertions.assertEquals(timeTableService.getGroupTimeTable(group.get()),
				timeTableService.getStudentTimeTable(student.get()));
	}

}