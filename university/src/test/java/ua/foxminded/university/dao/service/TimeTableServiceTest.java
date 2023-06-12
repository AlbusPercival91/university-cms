package ua.foxminded.university.dao.service;

import java.util.Optional;
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
import ua.foxminded.university.dao.entities.Admin;
import ua.foxminded.university.dao.entities.ClassRoom;
import ua.foxminded.university.dao.entities.Course;
import ua.foxminded.university.dao.entities.Department;
import ua.foxminded.university.dao.entities.Faculty;
import ua.foxminded.university.dao.entities.Group;
import ua.foxminded.university.dao.entities.Staff;
import ua.foxminded.university.dao.entities.Student;
import ua.foxminded.university.dao.entities.Teacher;
import ua.foxminded.university.dao.entities.TimeTable;
import ua.foxminded.university.dao.interfaces.AdminRepository;
import ua.foxminded.university.dao.interfaces.ClassRoomRepository;
import ua.foxminded.university.dao.interfaces.CourseRepository;
import ua.foxminded.university.dao.interfaces.DepartmentRepository;
import ua.foxminded.university.dao.interfaces.FacultyRepository;
import ua.foxminded.university.dao.interfaces.GroupRepository;
import ua.foxminded.university.dao.interfaces.StaffRepository;
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

	@Autowired
	private FacultyRepository facultyRepository;

	@Autowired
	private DepartmentRepository departmentRepository;

	@Autowired
	private AdminRepository adminRepository;

	@Autowired
	private StaffRepository staffRepository;

	@Test
	void testTimeTable() {
		Optional<TimeTable> course = timeTableRepository.findById(3);
		Assertions.assertEquals("Hello", course.toString());
	}

	@Test
	void testClassRoom() {
		Optional<ClassRoom> course = classRoomRepository.findById(3);
		Assertions.assertEquals("Hello", course.toString());
	}

	@Test
	void testCourse() {
		Optional<Course> course = courseRepository.findById(3);
		Assertions.assertEquals("Hello", course.toString());
	}

	@Test
	void testTeacher() {
		Optional<Teacher> course = teacherRepository.findById(3);
		Assertions.assertEquals("Hello", course.toString());
	}

	@Test
	void testStudent() {
		Optional<Student> course = studentRepository.findById(3);
		Assertions.assertEquals("Hello", course.toString());
	}

	@Test
	void testGroup() {
		Optional<Group> course = groupRepository.findById(3);
		Assertions.assertEquals("Hello", course.toString());
	}

	@Test
	void testFaculty() {
		Optional<Faculty> course = facultyRepository.findById(3);
		Assertions.assertEquals("Hello", course.toString());
	}

	@Test
	void testDep() {
		Optional<Department> course = departmentRepository.findById(3);
		Assertions.assertEquals("Hello", course.toString());
	}

	@Test
	void testAdmin() {
		Optional<Admin> course = adminRepository.findById(3);
		Assertions.assertEquals("Hello", course.toString());
	}

	@Test
	void testStaff() {
		Optional<Staff> course = staffRepository.findById(3);
		Assertions.assertEquals("Hello", course.toString());
	}

}