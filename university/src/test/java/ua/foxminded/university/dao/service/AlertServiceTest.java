package ua.foxminded.university.dao.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import java.time.LocalDateTime;
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
import ua.foxminded.university.dao.entities.Admin;
import ua.foxminded.university.dao.entities.Alert;
import ua.foxminded.university.dao.entities.Course;
import ua.foxminded.university.dao.entities.Department;
import ua.foxminded.university.dao.entities.Faculty;
import ua.foxminded.university.dao.entities.Staff;
import ua.foxminded.university.dao.entities.Student;
import ua.foxminded.university.dao.entities.Teacher;
import ua.foxminded.university.validation.Message;
import ua.foxminded.university.validation.UniqueEmailValidator;

@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = { AlertService.class,
        TeacherService.class, StudentService.class, GroupService.class, CourseService.class, StaffService.class,
        AdminService.class, DepartmentService.class, FacultyService.class, UniqueEmailValidator.class }))
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test-container")
@Sql(scripts = { "/drop_data.sql", "/init_tables.sql",
        "/insert_test_data.sql", }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class AlertServiceTest {

    @Autowired
    private AlertService alertService;

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private StaffService staffService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private FacultyService facultyService;

    @Autowired
    private DepartmentService departmentService;

    @ParameterizedTest
    @CsvSource({ "1, Test Alert" })
    void testGetAllTeacherAlerts_ShouldReturnAllTeacherAlerts(int teacherId, String message) {
        Optional<Teacher> optionalTeacher = teacherService.findTeacherById(teacherId);

        alertService.createTeacherAlert(LocalDateTime.now(), optionalTeacher.get().getId(), message);

        Assertions.assertTrue(
                alertService.getAllTeacherAlerts(optionalTeacher.get()).get(0).getMessage().contains(message));
    }

    @ParameterizedTest
    @CsvSource({ "12, Test Alert" })
    void testGetAllTeacherAlerts_WhenTeacherNotFound_ShouldThrowNoSuchElementException(int teacherId, String message) {
        Exception noSuchElementException = assertThrows(Exception.class,
                () -> alertService.createTeacherAlert(LocalDateTime.now(), 12, message));
        Assertions.assertEquals(Message.TEACHER_NOT_FOUND, noSuchElementException.getMessage());
    }

    @ParameterizedTest
    @CsvSource({ "1, Test Alert" })
    void testGetAllStudentAlerts_ShouldReturnAllStudentAlerts(int studentId, String message) {
        Optional<Student> optionalStudent = studentService.findStudentById(studentId);

        alertService.createStudentAlert(LocalDateTime.now(), optionalStudent.get().getId(), message);

        Assertions.assertTrue(
                alertService.getAllStudentAlerts(optionalStudent.get()).get(0).getMessage().contains(message));
    }

    @ParameterizedTest
    @CsvSource({ "12, Test Alert" })
    void testGetAllStudentAlerts_WhenStudentNotFound_ShouldThrowNoSuchElementException(int studentId, String message) {
        Exception noSuchElementException = assertThrows(Exception.class,
                () -> alertService.createStudentAlert(LocalDateTime.now(), 12, message));
        Assertions.assertEquals(Message.STUDENT_NOT_FOUND, noSuchElementException.getMessage());
    }

    @ParameterizedTest
    @CsvSource({ "1, Test Alert" })
    void testGetAllStudentAlerts_WhenCreateGroupAlert_ShouldReturnAllGroupAlerts(int studentId, String message) {
        Optional<Student> optionalStudent = studentService.findStudentById(studentId);

        alertService.createGroupAlert(LocalDateTime.now(), optionalStudent.get().getGroup().getId(), message);

        Assertions.assertTrue(
                alertService.getAllStudentAlerts(optionalStudent.get()).get(0).getMessage().contains(message));
    }

    @ParameterizedTest
    @CsvSource({ "1, 1, 1, Test Alert" })
    void testGetAllStudentAndTeacherAlerts_WhenCreateCourseAlert_ShouldReturnAllCourseAlerts(int studentId,
            int teacherId, int courseId, String message) {
        Optional<Student> optionalStudent = studentService.findStudentById(studentId);
        Optional<Teacher> optionalTeacher = teacherService.findTeacherById(teacherId);
        Optional<Course> optionalCourse = courseService.findCourseById(courseId);

        teacherService.addTeacherToTheCourse(teacherId, optionalCourse.get().getCourseName());
        studentService.addStudentToTheCourse(studentId, optionalCourse.get().getCourseName());

        alertService.createCourseAlert(LocalDateTime.now(), optionalCourse.get().getId(), message);

        Assertions.assertTrue(
                alertService.getAllStudentAlerts(optionalStudent.get()).get(0).getMessage().contains(message));

        Assertions.assertTrue(
                alertService.getAllTeacherAlerts(optionalTeacher.get()).get(0).getMessage().contains(message));
    }

    @ParameterizedTest
    @CsvSource({ "1, 1, 1, Test Alert" })
    void testGetAllStudentAndTeacherAlerts_WhenCreateFacultyAlert_ShouldReturnAllFacultyAlerts(int studentId,
            int teacherId, int facultyId, String message) {
        Optional<Student> optionalStudent = studentService.findStudentById(studentId);
        Optional<Teacher> optionalTeacher = teacherService.findTeacherById(teacherId);
        Optional<Faculty> optionalFaculty = facultyService.findFacultyById(facultyId);

        alertService.createFacultyAlert(LocalDateTime.now(), optionalFaculty.get().getId(), message);

        Assertions.assertTrue(
                alertService.getAllStudentAlerts(optionalStudent.get()).get(0).getMessage().contains(message));

        Assertions.assertTrue(
                alertService.getAllTeacherAlerts(optionalTeacher.get()).get(0).getMessage().contains(message));
    }

    @ParameterizedTest
    @CsvSource({ "1, 1, 1, Test Alert" })
    void testGetAllTeacherAlerts_WhenCreateDepartmentAlert_ShouldReturnAllDepartmentAlerts(int teacherId,
            int departmentId, String message) {
        Optional<Teacher> optionalTeacher = teacherService.findTeacherById(teacherId);
        Optional<Department> optionalDepartment = departmentService.findDepartmentById(departmentId);

        alertService.createDepartmentAlert(LocalDateTime.now(), optionalDepartment.get().getId(), message);

        Assertions.assertTrue(
                alertService.getAllTeacherAlerts(optionalTeacher.get()).get(0).getMessage().contains(message));
    }

    @ParameterizedTest
    @CsvSource({ "1, Test Alert" })
    void testGetAllStaffAlerts_ShouldReturnAllStaffAlerts(int staffId, String message) {
        Optional<Staff> optionalStaff = staffService.findStaffById(staffId);

        alertService.createStaffAlert(LocalDateTime.now(), optionalStaff.get().getId(), message);

        Assertions
                .assertTrue(alertService.getAllStaffAlerts(optionalStaff.get()).get(0).getMessage().contains(message));
    }

    @ParameterizedTest
    @CsvSource({ "12, Test Aler" })
    void testGetAllStaffAlerts_WhenStaffNotFound_ShouldThrowNoSuchElementException(int staffId, String message) {
        Exception noSuchElementException = assertThrows(Exception.class,
                () -> alertService.createStaffAlert(LocalDateTime.now(), 12, message));
        Assertions.assertEquals(Message.STAFF_NOT_FOUND, noSuchElementException.getMessage());
    }

    @ParameterizedTest
    @CsvSource({ "1, Test Alert" })
    void testGetAllAdminAlerts_ShouldReturnAllAdminAlerts(int adminId, String message) {
        Optional<Admin> optionalAdmin = adminService.findAdminById(adminId);

        alertService.createAdminAlert(LocalDateTime.now(), optionalAdmin.get().getId(), message);

        Assertions
                .assertTrue(alertService.getAllAdminAlerts(optionalAdmin.get()).get(0).getMessage().contains(message));
    }

    @ParameterizedTest
    @CsvSource({ "12, Test Alert" })
    void testGetAllAdminAlerts_WhenAdminNotFound_ShouldThrowNoSuchElementException(int adminId, String message) {
        Exception noSuchElementException = assertThrows(Exception.class,
                () -> alertService.createAdminAlert(LocalDateTime.now(), 12, message));
        Assertions.assertEquals(Message.ADMIN_NOT_FOUND, noSuchElementException.getMessage());
    }

    @ParameterizedTest
    @CsvSource({ "1, 1, Test Alert" })
    void testDeleteAlertById_Success(int alertId, int adminId, String message) {
        Optional<Admin> optionalAdmin = adminService.findAdminById(adminId);

        alertService.createAdminAlert(LocalDateTime.now(), optionalAdmin.get().getId(), message);
        Optional<Alert> optionalAlert = alertService.findAlertById(alertId);

        Assertions
                .assertTrue(alertService.getAllAdminAlerts(optionalAdmin.get()).get(0).getMessage().contains(message));
        Assertions.assertEquals(alertId, alertService.deleteAlertById(optionalAlert.get().getId()));
        Assertions.assertTrue(alertService.getAllAdminAlerts(optionalAdmin.get()).isEmpty());
    }

    @Test
    void testDeleteAlertById_WhenIdNotFound_ShouldThrowNoSuchElementException() {
        Exception noSuchElementException = assertThrows(Exception.class, () -> alertService.deleteAlertById(4));
        Assertions.assertEquals(Message.ALERT_NOT_FOUND, noSuchElementException.getMessage());
    }

    @ParameterizedTest
    @CsvSource({ "1, 1, 1, 1, Test Alert" })
    void testCeateBroadcastAlert_ShouldSendlertsToAllUsers(int adminId, int studentId, int teacherId, int staffId,
            String message) {
        Optional<Admin> optionalAdmin = adminService.findAdminById(adminId);
        Optional<Student> optionalStudent = studentService.findStudentById(studentId);
        Optional<Teacher> optionalTeacher = teacherService.findTeacherById(teacherId);
        Optional<Staff> optionalStaff = staffService.findStaffById(staffId);

        alertService.createBroadcastAlert(LocalDateTime.now(), message);

        Assertions
                .assertTrue(alertService.getAllAdminAlerts(optionalAdmin.get()).get(0).getMessage().contains(message));
        Assertions.assertTrue(
                alertService.getAllStudentAlerts(optionalStudent.get()).get(0).getMessage().contains(message));
        Assertions.assertTrue(
                alertService.getAllTeacherAlerts(optionalTeacher.get()).get(0).getMessage().contains(message));
        Assertions
                .assertTrue(alertService.getAllStaffAlerts(optionalStaff.get()).get(0).getMessage().contains(message));
    }

}
