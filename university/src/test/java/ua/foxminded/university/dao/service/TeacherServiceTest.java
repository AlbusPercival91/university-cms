package ua.foxminded.university.dao.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.ArrayList;
import java.util.List;
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
import ua.foxminded.university.dao.entities.Course;
import ua.foxminded.university.dao.entities.Department;
import ua.foxminded.university.dao.entities.Teacher;
import ua.foxminded.university.dao.interfaces.CourseRepository;
import ua.foxminded.university.dao.interfaces.DepartmentRepository;
import ua.foxminded.university.dao.interfaces.TeacherRepository;
import ua.foxminded.university.security.UserRole;
import ua.foxminded.university.validation.UniqueEmailValidator;

@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = { TeacherService.class,
        UniqueEmailValidator.class }))
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test-container")
@Sql(scripts = { "/drop_data.sql", "/init_tables.sql",
        "/insert_test_data.sql", }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class TeacherServiceTest {

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @ParameterizedTest
    @CsvSource({ "1", "2", "3" })
    void testDeleteTeacherById_ShouldReturnTeacherId(int teacherId) {
        Assertions.assertEquals(teacherId, teacherRepository.findById(teacherId).get().getId());
        Assertions.assertEquals(teacherId, teacherService.deleteTeacherById(teacherId));
    }

    @Test
    void testDeleteTeacherById_WhenIdNotFound_ShouldThrowNoSuchElementException() {
        Exception noSuchElementException = assertThrows(Exception.class, () -> teacherService.deleteTeacherById(4));
        Assertions.assertEquals("Teacher not found", noSuchElementException.getMessage());
    }

    @ParameterizedTest
    @CsvSource({ "1", "2", "3" })
    void testUpdateTeacherById_ShouldReturnUpdatedTeacher(int teacherId) {
        Teacher expectedTeacher = new Teacher("Severus", "Snape", false, "snape@fakemail.com", "1234", null);
        expectedTeacher.setId(teacherId);
        expectedTeacher.setRole(UserRole.TEACHER);

        Assertions.assertEquals(expectedTeacher, teacherService.updateTeacherById(teacherId, expectedTeacher));
    }

    @Test
    void testUpdateTeacherById_WhenIdNotFound_ShouldThrowNoSuchElementException() {
        Teacher expectedTeacher = new Teacher("Severus", "Snape", false, "snape@fakemail.com", "1234", null);
        expectedTeacher.setId(1);

        Exception noSuchElementException = assertThrows(Exception.class,
                () -> teacherService.updateTeacherById(4, expectedTeacher));
        Assertions.assertEquals("Teacher not found", noSuchElementException.getMessage());
    }

    @ParameterizedTest
    @CsvSource({ "3, 3", "2, 2", "1, 1" })
    void testFindTeachersRelatedToCourse_ShouldReturnListOfTeachersRelatdToCourse(int courseId, int departmentId) {
        List<Teacher> expectedTeachersList = new ArrayList<>();
        Optional<Course> course = courseRepository.findById(courseId);
        Optional<Department> department = departmentRepository.findById(departmentId);
        Teacher teacher = new Teacher("Albus", "Dumbledore", true, "albus@gmail.com", "1234", department.get());
        expectedTeachersList.add(teacher);
        teacherService.createAndAssignTeacherToCourse(teacher, course.get());

        Assertions.assertEquals(expectedTeachersList,
                teacherService.findTeachersRelatedToCourse(course.get().getCourseName()));

    }

    @Test
    void testRemoveTeacherFromCourse_ShouldReturnOneTeacherRemovedFromCourse() {
        Optional<Course> course = courseRepository.findById(1);
        Optional<Course> additionalCourse = courseRepository.findById(3);
        Optional<Department> department = departmentRepository.findById(1);
        Teacher teacher = new Teacher("Albus", "Dumbledore", true, "albus@gmail.com", "1234", department.get());
        teacherService.createAndAssignTeacherToCourse(teacher, course.get());
        teacherService.addTeacherToTheCourse(teacher.getId(), additionalCourse.get().getCourseName());

        Assertions.assertEquals(1,
                teacherService.removeTeacherFromCourse(teacher.getId(), additionalCourse.get().getCourseName()));
    }

    @Test
    void testRemoveTeacherFromCourse_WhenTeacherIsNotRelatedToCourse_ShouldThrowIllegalStateException() {
        Optional<Course> course = courseRepository.findById(1);
        Optional<Course> additionalCourse = courseRepository.findById(3);
        Optional<Department> department = departmentRepository.findById(1);
        Teacher teacher = new Teacher("Albus", "Dumbledore", true, "albus@gmail.com", "1234", department.get());
        teacherService.createAndAssignTeacherToCourse(teacher, course.get());

        Exception illegalStateException = assertThrows(Exception.class,
                () -> teacherService.removeTeacherFromCourse(teacher.getId(), additionalCourse.get().getCourseName()));
        Assertions.assertEquals("User is not related with this Course!", illegalStateException.getMessage());
    }

}
