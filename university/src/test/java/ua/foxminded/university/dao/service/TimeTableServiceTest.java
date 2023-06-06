package ua.foxminded.university.dao.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
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
import ua.foxminded.university.dao.interfaces.ClassRoomRepository;
import ua.foxminded.university.dao.interfaces.CourseRepository;
import ua.foxminded.university.dao.interfaces.DepartmentRepository;
import ua.foxminded.university.dao.interfaces.FacultyRepository;
import ua.foxminded.university.dao.interfaces.GroupRepository;
import ua.foxminded.university.dao.interfaces.TeacherRepository;
import ua.foxminded.university.dao.interfaces.TimeTableRepository;

@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
		TimeTableService.class }))
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test-container")
@Sql(scripts = { "/drop_data.sql", "/init_tables.sql", }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class TimeTableServiceTest {

	@Autowired
	private TimeTableService timeTableService;

	@Autowired
	private TimeTableRepository timeTableRepository;

	@Autowired
	private TeacherRepository teacherRepository;

	@Autowired
	private DepartmentRepository departmentRepository;

	@Autowired
	private FacultyRepository facultyRepository;

	@Autowired
	private GroupRepository groupRepository;

	@Autowired
	private CourseRepository courseRepository;

	@Autowired
	private ClassRoomRepository classRoomRepository;

	@Test
	void testGetTeacherTimeTableByDate() {
		List<TimeTable> timeTableList = new ArrayList<>();

		Faculty scienceFaculty = new Faculty("Faculty of Science");
		scienceFaculty.setId(1);
		facultyRepository.save(scienceFaculty);

		Department mathematicsDepartment = new Department("Mathematics Department", scienceFaculty);
		mathematicsDepartment.setId(1);
		departmentRepository.save(mathematicsDepartment);

		Course mathCourse = new Course("Mathematics", "Advanced mathematics course");
		mathCourse.setId(1);
		courseRepository.save(mathCourse);

		Teacher teacher = new Teacher("James", "Wilson", true, "james.wilson@example.com", "password11",
				mathematicsDepartment, mathCourse);
		teacher.setId(1);
		teacherRepository.save(teacher);

		Group a = new Group("Group A", scienceFaculty);
		a.setId(1);
		groupRepository.save(a);

		ClassRoom room = new ClassRoom("Main Street", 1, 101);
		room.setId(1);
		classRoomRepository.save(room);

		LocalDate dateStart = LocalDate.now();

		LocalTime timeStart = LocalTime.of(13, 30);
		LocalTime timeEnd = LocalTime.of(15, 30);

		TimeTable expectedFirst = new TimeTable(dateStart, timeStart, timeEnd, teacher, mathCourse, a, room);
		expectedFirst.setId(1);
		timeTableList.add(expectedFirst);
		timeTableRepository.save(expectedFirst);

		// create second timetable

		Faculty literatureFaculty = new Faculty("Faculty of Literature");
		literatureFaculty.setId(2);
		facultyRepository.save(literatureFaculty);

		Department englishDepartment = new Department("English Department", literatureFaculty);
		englishDepartment.setId(2);
		departmentRepository.save(englishDepartment);

		Course englishCourse = new Course("English", "Advanced English course");
		englishCourse.setId(2);
		courseRepository.save(englishCourse);

		Teacher teacherEnglish = new Teacher("Connor", "Tylor", true, "connor.ty@example.com", "1234",
				englishDepartment, englishCourse);
		teacherEnglish.setId(2);
		teacherRepository.save(teacherEnglish);

		Group b = new Group("Group b", literatureFaculty);
		b.setId(2);
		groupRepository.save(b);

		ClassRoom room2 = new ClassRoom("Forest Street", 2, 21);
		room2.setId(2);
		classRoomRepository.save(room2);

		LocalDate dateEnd = dateStart.plusDays(2);
		LocalTime timeStart2 = LocalTime.of(12, 00);
		LocalTime timeEnd2 = LocalTime.of(14, 00);

		TimeTable expectedSecond = new TimeTable(dateEnd, timeStart2, timeEnd2, teacher, englishCourse, b, room2);
		expectedSecond.setId(2);
		timeTableList.add(expectedSecond);
		timeTableRepository.save(expectedSecond);
		Assertions.assertEquals(timeTableList, timeTableService.getTeacherTimeTableByDate(dateStart, dateEnd, teacher));
	}

	@Test
	void testGetTeacherTimeTable() {
		List<TimeTable> timeTableList = new ArrayList<>();

		Faculty scienceFaculty = new Faculty("Faculty of Science");
		scienceFaculty.setId(1);
		facultyRepository.save(scienceFaculty);

		Department mathematicsDepartment = new Department("Mathematics Department", scienceFaculty);
		mathematicsDepartment.setId(1);
		departmentRepository.save(mathematicsDepartment);

		Course mathCourse = new Course("Mathematics", "Advanced mathematics course");
		mathCourse.setId(1);
		courseRepository.save(mathCourse);

		Teacher teacher = new Teacher("James", "Wilson", true, "james.wilson@example.com", "password11",
				mathematicsDepartment, mathCourse);
		teacher.setId(1);
		teacherRepository.save(teacher);

		Group a = new Group("Group A", scienceFaculty);
		a.setId(1);
		groupRepository.save(a);

		ClassRoom room = new ClassRoom("Main Street", 1, 101);
		room.setId(1);
		classRoomRepository.save(room);

		LocalDate dateStart = LocalDate.now();

		LocalTime timeStart = LocalTime.of(12, 00);
		LocalTime timeEnd = LocalTime.of(13, 30);

		TimeTable expectedFirst = new TimeTable(dateStart, timeStart, timeEnd, teacher, mathCourse, a, room);
		expectedFirst.setId(1);
		timeTableList.add(expectedFirst);
		timeTableRepository.save(expectedFirst);

		// create second timetable

		Faculty literatureFaculty = new Faculty("Faculty of Literature");
		literatureFaculty.setId(2);
		facultyRepository.save(literatureFaculty);

		Department englishDepartment = new Department("English Department", literatureFaculty);
		englishDepartment.setId(2);
		departmentRepository.save(englishDepartment);

		Course englishCourse = new Course("English", "Advanced English course");
		englishCourse.setId(2);
		courseRepository.save(englishCourse);

		Teacher teacherEnglish = new Teacher("Connor", "Tylor", true, "connor.ty@example.com", "1234",
				englishDepartment, englishCourse);
		teacherEnglish.setId(2);
		teacherRepository.save(teacherEnglish);

		Group b = new Group("Group b", literatureFaculty);
		b.setId(2);
		groupRepository.save(b);

		ClassRoom room2 = new ClassRoom("Forest Street", 2, 21);
		room2.setId(2);
		classRoomRepository.save(room2);

		LocalDate dateEnd = dateStart.plusDays(2);
		LocalTime timeStart2 = LocalTime.of(15, 30);
		LocalTime timeEnd2 = LocalTime.of(17, 00);

		TimeTable expectedSecond = new TimeTable(dateEnd, timeStart2, timeEnd2, teacher, englishCourse, b, room2);
		expectedSecond.setId(2);
		timeTableList.add(expectedSecond);
		timeTableRepository.save(expectedSecond);

		Assertions.assertEquals(timeTableList, timeTableService.getTeacherTimeTable(teacher));
	}
}