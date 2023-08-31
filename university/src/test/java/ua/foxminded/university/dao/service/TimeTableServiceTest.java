package ua.foxminded.university.dao.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
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
import ua.foxminded.university.dao.entities.Group;
import ua.foxminded.university.dao.entities.Student;
import ua.foxminded.university.dao.entities.Teacher;
import ua.foxminded.university.dao.entities.TimeTable;
import ua.foxminded.university.dao.interfaces.StudentRepository;
import ua.foxminded.university.dao.interfaces.TeacherRepository;
import ua.foxminded.university.dao.interfaces.TimeTableRepository;
import ua.foxminded.university.validation.TimeTableValidator;
import ua.foxminded.university.validation.UniqueEmailValidator;

@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
        TimeTableService.class, TimeTableBuilder.class, UniqueEmailValidator.class, TimeTableValidator.class }))
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

    @Autowired
    private TeacherRepository teacherRepository;

    @ParameterizedTest
    @CsvSource({ "2023-09-01, 09:00, 10:30, 1, 1, 1, 1", "2023-09-01, 12:00, 13:30, 2, 2, 2, 2",
            "2023-09-02, 09:00, 10:30, 3, 3, 3, 3", "2023-09-02, 12:00, 13:30, 1, 1, 2, 3" })
    void testCreateGroupTimeTable_ShouldSaveTimeTableToDatabase(LocalDate date, LocalTime timeFrom, LocalTime timeTo,
            int teacherId, int courseId, int groupId, int classRoomId) {
        teacherRepository.addTeacherToTheCourse(teacherId, "Mathematics");
        teacherRepository.addTeacherToTheCourse(teacherId, "Physics");
        teacherRepository.addTeacherToTheCourse(teacherId, "Chemistry");

        TimeTable timeTable1 = timeTableBuilder.saveGroupTimeTable(date, timeFrom, timeTo, teacherId, courseId, groupId,
                classRoomId);

        Assertions.assertEquals(timeTableRepository.findById(1).get(), timeTable1);
    }

    @ParameterizedTest
    @CsvSource({ "2023-09-01, 09:00, 10:30, 1, 1, 1, 1", "2023-09-01, 12:00, 13:30, 2, 2, 2, 2",
            "2023-09-02, 09:00, 10:30, 3, 3, 3, 3", "2023-09-02, 12:00, 13:30, 1, 1, 2, 3" })
    void testCreateGroupTimeTable_WhenClassRoomIsBusy_ShouldThrowIllegalStateException(LocalDate date,
            LocalTime timeFrom, LocalTime timeTo, int teacherId, int courseId, int groupId, int classRoomId) {
        teacherRepository.addTeacherToTheCourse(teacherId, "Mathematics");
        teacherRepository.addTeacherToTheCourse(teacherId, "Physics");
        teacherRepository.addTeacherToTheCourse(teacherId, "Chemistry");
        timeTableBuilder.saveGroupTimeTable(date, timeFrom, timeTo, teacherId, courseId, groupId, classRoomId);

        Exception timeTableValidationException = assertThrows(Exception.class, () -> timeTableBuilder
                .saveGroupTimeTable(date, timeFrom, timeTo, teacherId, courseId, groupId, classRoomId));
        Assertions.assertEquals("Validation failed while creating TimeTable",
                timeTableValidationException.getMessage());
    }

    @ParameterizedTest
    @CsvSource({ "2023-09-01, 09:00, 10:30, 1, 1, 1", "2023-09-01, 12:00, 13:30, 2, 2, 2",
            "2023-09-02, 09:00, 10:30, 3, 3, 3", "2023-09-02, 12:00, 13:30, 1, 1, 2" })
    void testCreateTimeTableForStudentsAtCourse_ShouldSaveTimeTableToDatabase(LocalDate date, LocalTime timeFrom,
            LocalTime timeTo, int teacherId, int courseId, int classRoomId) {
        studentRepository.addStudentToTheCourse(1, "Mathematics");
        studentRepository.addStudentToTheCourse(2, "Mathematics");
        studentRepository.addStudentToTheCourse(3, "Physics");
        studentRepository.addStudentToTheCourse(1, "Chemistry");

        teacherRepository.addTeacherToTheCourse(teacherId, "Mathematics");
        teacherRepository.addTeacherToTheCourse(teacherId, "Physics");
        teacherRepository.addTeacherToTheCourse(teacherId, "Chemistry");

        TimeTable timeTable = timeTableBuilder.saveTimeTableForStudentsAtCourse(date, timeFrom, timeTo, teacherId,
                courseId, classRoomId);

        Assertions.assertEquals(timeTableRepository.findById(1).get(), timeTable);
    }

    @ParameterizedTest
    @CsvSource({ "2023-09-01, 09:00, 10:30, 1, 1, 1", "2023-09-01, 12:00, 13:30, 2, 2, 2",
            "2023-09-02, 09:00, 10:30, 3, 3, 3", "2023-09-02, 12:00, 13:30, 1, 1, 2" })
    void testCreateTimeTableForStudentsAtCourse_WhenClassRoomIsBusy_ShouldThrowIllegalStateException(LocalDate date,
            LocalTime timeFrom, LocalTime timeTo, int teacherId, int courseId, int classRoomId) {
        studentRepository.addStudentToTheCourse(1, "Mathematics");
        studentRepository.addStudentToTheCourse(2, "Mathematics");
        studentRepository.addStudentToTheCourse(3, "Physics");
        studentRepository.addStudentToTheCourse(1, "Chemistry");

        teacherRepository.addTeacherToTheCourse(teacherId, "Mathematics");
        teacherRepository.addTeacherToTheCourse(teacherId, "Physics");
        teacherRepository.addTeacherToTheCourse(teacherId, "Chemistry");

        timeTableBuilder.saveTimeTableForStudentsAtCourse(date, timeFrom, timeTo, teacherId, courseId, classRoomId);

        Exception illegalStateException = assertThrows(Exception.class, () -> timeTableBuilder
                .saveTimeTableForStudentsAtCourse(date, timeFrom, timeTo, teacherId, courseId, classRoomId));
        Assertions.assertEquals("Validation failed while creating TimeTable", illegalStateException.getMessage());
    }

    @ParameterizedTest
    @CsvSource({ "2023-09-01, 09:00, 10:30, 1, 1, 1", "2023-09-01, 12:00, 13:30, 2, 2, 2",
            "2023-09-02, 09:00, 10:30, 3, 3, 3", "2023-09-02, 12:00, 13:30, 1, 1, 2" })
    void testCreateTimeTableForStudentsAtCourse_WhenStudentsNotFound_ShouldThrowNoSuchElementException(LocalDate date,
            LocalTime timeFrom, LocalTime timeTo, int teacherId, int courseId, int classRoomId) {

        Exception noSuchElementException = assertThrows(Exception.class, () -> timeTableBuilder
                .saveTimeTableForStudentsAtCourse(date, timeFrom, timeTo, teacherId, courseId, classRoomId));
        Assertions.assertEquals("Teacher is not assigned with such Course", noSuchElementException.getMessage());
    }

    @ParameterizedTest
    @CsvSource({ "2023-09-01, 09:00, 10:30, 1, 1, 2, 1, 1, 1", "2023-09-01, 12:00, 13:30, 2, 2, 2, 2, 2, 2",
            "2023-09-02, 09:00, 10:30, 3, 3, 3, 3, 3, 3", "2023-09-02, 12:00, 13:30, 1, 1, 2, 3, 1, 3" })
    void testGetStudentTimeTable_ShouldReturnAllTimeTablesForStudent(LocalDate date, LocalTime timeFrom,
            LocalTime timeTo, int teacherId, int firstCourseId, int secondCourseId, int groupId, int classRoomId,
            int studentId) {
        studentRepository.addStudentToTheCourse(studentId, "Mathematics");
        studentRepository.addStudentToTheCourse(studentId, "Physics");
        studentRepository.addStudentToTheCourse(studentId, "Chemistry");

        teacherRepository.addTeacherToTheCourse(teacherId, "Mathematics");
        teacherRepository.addTeacherToTheCourse(teacherId, "Physics");
        teacherRepository.addTeacherToTheCourse(teacherId, "Chemistry");

        TimeTable timeTable1 = timeTableBuilder.saveGroupTimeTable(date, timeFrom, timeTo, teacherId, firstCourseId,
                groupId, classRoomId);
        TimeTable timeTable2 = timeTableBuilder.saveGroupTimeTable(date, timeFrom.plusHours(2), timeTo.plusHours(2),
                teacherId, secondCourseId, groupId, classRoomId);
        TimeTable timeTable3 = timeTableBuilder.saveTimeTableForStudentsAtCourse(date, timeFrom.plusHours(5),
                timeTo.plusHours(5), teacherId, firstCourseId, classRoomId);
        TimeTable timeTable4 = timeTableBuilder.saveTimeTableForStudentsAtCourse(date.plusDays(1),
                timeFrom.plusHours(1), timeTo.plusHours(1), teacherId, firstCourseId, classRoomId);
        List<TimeTable> expectedTimeTables = new ArrayList<>() {
            private static final long serialVersionUID = 1L;
            {
                add(timeTable1);
                add(timeTable2);
                add(timeTable3);
                add(timeTable4);
            }
        };

        Assertions.assertEquals(expectedTimeTables, timeTableService.getStudentTimeTable(studentId));
    }

    @ParameterizedTest
    @CsvSource({ "2023-09-01, 09:00, 10:30, 1, 1, 2, 1, 1, 1", "2023-09-01, 12:00, 13:30, 2, 2, 2, 2, 2, 2",
            "2023-09-02, 09:00, 10:30, 3, 3, 3, 3, 3, 3", "2023-09-02, 12:00, 13:30, 1, 1, 2, 3, 1, 3" })
    void testGetStudentTimeTableByDate_ShouldReturnAllTimeTablesForStudentWithDateRange(LocalDate date,
            LocalTime timeFrom, LocalTime timeTo, int teacherId, int firstCourseId, int secondCourseId, int groupId,
            int classRoomId, int studentId) {
        studentRepository.addStudentToTheCourse(studentId, "Mathematics");
        studentRepository.addStudentToTheCourse(studentId, "Physics");
        studentRepository.addStudentToTheCourse(studentId, "Chemistry");

        teacherRepository.addTeacherToTheCourse(teacherId, "Mathematics");
        teacherRepository.addTeacherToTheCourse(teacherId, "Physics");
        teacherRepository.addTeacherToTheCourse(teacherId, "Chemistry");

        TimeTable timeTable1 = timeTableBuilder.saveGroupTimeTable(date, timeFrom, timeTo, teacherId, firstCourseId,
                groupId, classRoomId);
        TimeTable timeTable2 = timeTableBuilder.saveGroupTimeTable(date, timeFrom.plusHours(2), timeTo.plusHours(2),
                teacherId, secondCourseId, groupId, classRoomId);
        TimeTable timeTable3 = timeTableBuilder.saveTimeTableForStudentsAtCourse(date, timeFrom.plusHours(5),
                timeTo.plusHours(5), teacherId, firstCourseId, classRoomId);
        timeTableBuilder.saveTimeTableForStudentsAtCourse(date.plusDays(1), timeFrom.plusHours(1), timeTo.plusHours(1),
                teacherId, firstCourseId, classRoomId);
        List<TimeTable> expectedTimeTables = new ArrayList<>() {
            private static final long serialVersionUID = 1L;
            {
                add(timeTable1);
                add(timeTable2);
                add(timeTable3);
            }
        };

        Assertions.assertEquals(expectedTimeTables, timeTableService.getStudentTimeTableByDate(date, date, studentId));
    }

    @ParameterizedTest
    @CsvSource({ "2023-09-01, 09:00, 10:30, 1, 1, 1, 1 ", "2023-09-01, 12:00, 13:30, 2, 2, 2, 2",
            "2023-09-02, 09:00, 10:30, 3, 3, 3, 3", "2023-09-02, 12:00, 13:30, 1, 1, 2, 3" })
    void testGetTeacherTimeTable_ShouldReturnTeacherTimeTable(LocalDate date, LocalTime timeFrom, LocalTime timeTo,
            int teacherId, int courseId, int groupId, int classRoomId) {
        List<TimeTable> expectedTimeTableList = new ArrayList<>();

        IntStream.rangeClosed(1, 3).forEach(y -> {
            teacherRepository.addTeacherToTheCourse(y, "Mathematics");
            teacherRepository.addTeacherToTheCourse(y, "Physics");
            teacherRepository.addTeacherToTheCourse(y, "Chemistry");
        });

        TimeTable timeTable = timeTableBuilder.saveGroupTimeTable(date, timeFrom, timeTo, teacherId, courseId, groupId,
                classRoomId);
        expectedTimeTableList.add(timeTable);

        Assertions.assertEquals(expectedTimeTableList, timeTableService.getTeacherTimeTable(timeTable.getTeacher()));
    }

    @ParameterizedTest
    @CsvSource({ "2023-09-01, 09:00, 10:30, 1, 1, 1, 1 " })
    void testGetTeacherTimeTable_WhenTeacherNotFound_ShouldThrowNoSuchElementException(LocalDate date,
            LocalTime timeFrom, LocalTime timeTo, int teacherId, int courseId, int groupId, int classRoomId) {
        teacherRepository.addTeacherToTheCourse(teacherId, "Mathematics");
        timeTableBuilder.saveGroupTimeTable(date, timeFrom, timeTo, teacherId, courseId, groupId, classRoomId);

        Teacher fakeTeacher = new Teacher("Kevin", "Kell", false, "faketeacher@fakemail.com", "1234", null);

        Exception noSuchElementException = assertThrows(Exception.class,
                () -> timeTableService.getTeacherTimeTable(fakeTeacher));
        Assertions.assertEquals("Teacher not found", noSuchElementException.getMessage());
    }

    @ParameterizedTest
    @CsvSource({ "2023-09-01, 09:00, 10:30, 1, 1, 1, 1, 1", "2023-09-01, 12:00, 13:30, 2, 2, 2, 2, 2",
            "2023-09-02, 09:00, 10:30, 3, 3, 3, 3, 3", "2023-09-02, 12:00, 13:30, 1, 1, 3, 1, 3" })
    void testGetStudentsGroupTimeTable_ShouldReturnSameTimeTableForStudentAndHisGroup(LocalDate date,
            LocalTime timeFrom, LocalTime timeTo, int teacherId, int courseId, int groupId, int classRoomId,
            int studentId) {
        Optional<Student> student = studentRepository.findById(studentId);

        IntStream.rangeClosed(1, 3).forEach(y -> {
            teacherRepository.addTeacherToTheCourse(y, "Mathematics");
            teacherRepository.addTeacherToTheCourse(y, "Physics");
            teacherRepository.addTeacherToTheCourse(y, "Chemistry");
        });

        TimeTable timeTable = timeTableBuilder.saveGroupTimeTable(date, timeFrom, timeTo, teacherId, courseId, groupId,
                classRoomId);

        Assertions.assertEquals(timeTableService.getGroupTimeTable(timeTable.getGroup()),
                timeTableService.getStudentsGroupTimeTable(student.get()));
    }

    @ParameterizedTest
    @CsvSource({ "2023-09-01, 09:00, 10:30, 1, 1, 1, 1 " })
    void testGetStudentsGroupTimeTable_WhenStudentNotFound_ShouldThrowNoSuchElementException(LocalDate date,
            LocalTime timeFrom, LocalTime timeTo, int teacherId, int courseId, int groupId, int classRoomId) {
        teacherRepository.addTeacherToTheCourse(teacherId, "Mathematics");

        timeTableBuilder.saveGroupTimeTable(date, timeFrom, timeTo, teacherId, courseId, groupId, classRoomId);
        Student fakeStudent = new Student("Kevin", "Kell", true, "faketeacher@fakemail.com", "1234", null);

        Exception noSuchElementException = assertThrows(Exception.class,
                () -> timeTableService.getStudentsGroupTimeTable(fakeStudent));
        Assertions.assertEquals("Student not found", noSuchElementException.getMessage());
    }

    @ParameterizedTest
    @CsvSource({ "2023-09-01, 09:00, 10:30, 1, 1, 1, 1 " })
    void testGetGroupTimeTable_WhenGroupNotFound_ShouldThrowNoSuchElementException(LocalDate date, LocalTime timeFrom,
            LocalTime timeTo, int teacherId, int courseId, int groupId, int classRoomId) {
        teacherRepository.addTeacherToTheCourse(teacherId, "Mathematics");
        timeTableBuilder.saveGroupTimeTable(date, timeFrom, timeTo, teacherId, courseId, groupId, classRoomId);
        Group fakeGroup = new Group("fake-01", null);

        Exception noSuchElementException = assertThrows(Exception.class,
                () -> timeTableService.getGroupTimeTable(fakeGroup));
        Assertions.assertEquals("Group not found", noSuchElementException.getMessage());
    }

    @ParameterizedTest
    @CsvSource({ "2023-09-01, 09:00, 10:00, 1, 1, 1, 1" })
    void testGetTeacherTimeTableByDate_ShouldReturnTeacherTimeTable(LocalDate date, LocalTime timeFrom,
            LocalTime timeTo, int teacherId, int courseId, int groupId, int classRoomId) {
        TimeTable timeTable = new TimeTable();
        List<TimeTable> expectedTimeTableList = new ArrayList<>();

        IntStream.rangeClosed(1, 3).forEach(y -> {
            teacherRepository.addTeacherToTheCourse(y, "Mathematics");
            teacherRepository.addTeacherToTheCourse(y, "Physics");
            teacherRepository.addTeacherToTheCourse(y, "Chemistry");
        });

        for (int i = 0; i < 3; i++) {
            timeTable = timeTableBuilder.saveGroupTimeTable(date, timeFrom.plusHours(i), timeTo.plusHours(i), teacherId,
                    courseId, groupId++, classRoomId++);
            expectedTimeTableList.add(timeTable);
        }

        Assertions.assertEquals(expectedTimeTableList,
                timeTableService.getTeacherTimeTableByDate(date, date, timeTable.getTeacher()));
    }

    @ParameterizedTest
    @CsvSource({ "2023-09-01, 09:00, 10:30, 1, 1, 1, 1 " })
    void testGetTeacherTimeTableByDate_WhenTeacherNotFound_ShouldThrowNoSuchElementException(LocalDate date,
            LocalTime timeFrom, LocalTime timeTo, int teacherId, int courseId, int groupId, int classRoomId) {
        teacherRepository.addTeacherToTheCourse(teacherId, "Mathematics");

        timeTableBuilder.saveGroupTimeTable(date, timeFrom, timeTo, teacherId, courseId, groupId, classRoomId);
        Teacher fakeTeacher = new Teacher("Kevin", "Kell", false, "faketeacher@fakemail.com", "1234", null);

        Exception noSuchElementException = assertThrows(Exception.class,
                () -> timeTableService.getTeacherTimeTableByDate(date, date, fakeTeacher));
        Assertions.assertEquals("Teacher not found", noSuchElementException.getMessage());
    }

    @ParameterizedTest
    @CsvSource({ "2023-09-01, 09:00, 10:00, 1, 1, 1, 1, 1" })
    void testGetStudentsGroupTimeTableByDate_ShouldReturnSameTimeTableForStudentAndHisGroup(LocalDate date,
            LocalTime timeFrom, LocalTime timeTo, int teacherId, int courseId, int groupId, int classRoomId,
            int studentId) {
        TimeTable timeTable = new TimeTable();
        Optional<Student> student = studentRepository.findById(studentId);

        IntStream.rangeClosed(1, 3).forEach(y -> {
            teacherRepository.addTeacherToTheCourse(y, "Mathematics");
            teacherRepository.addTeacherToTheCourse(y, "Physics");
            teacherRepository.addTeacherToTheCourse(y, "Chemistry");
        });

        for (int i = 0; i < 3; i++) {
            timeTable = timeTableBuilder.saveGroupTimeTable(date, timeFrom.plusHours(i), timeTo.plusHours(i),
                    teacherId++, courseId++, groupId, classRoomId++);
        }
        Assertions.assertEquals(timeTableService.getGroupTimeTableByDate(date, date, timeTable.getGroup()),
                timeTableService.getStudentsGroupTimeTableByDate(date, date, student.get()));
    }

    @ParameterizedTest
    @CsvSource({ "2023-09-01, 09:00, 10:30, 1, 1, 1, 1 " })
    void testGetStudentsGroupTimeTableByDate_WhenStudentNotFound_ShouldThrowNoSuchElementException(LocalDate date,
            LocalTime timeFrom, LocalTime timeTo, int teacherId, int courseId, int groupId, int classRoomId) {
        teacherRepository.addTeacherToTheCourse(teacherId, "Mathematics");
        timeTableBuilder.saveGroupTimeTable(date, timeFrom, timeTo, teacherId, courseId, groupId, classRoomId);
        Student fakeStudent = new Student("Kevin", "Kell", true, "faketeacher@fakemail.com", "1234", null);

        Exception noSuchElementException = assertThrows(Exception.class,
                () -> timeTableService.getStudentsGroupTimeTableByDate(date, date, fakeStudent));
        Assertions.assertEquals("Student not found", noSuchElementException.getMessage());
    }

    @ParameterizedTest
    @CsvSource({ "2023-09-01, 09:00, 10:30, 1, 1, 1, 1 " })
    void testGetGroupTimeTableByDate_WhenGroupNotFound_ShouldThrowNoSuchElementException(LocalDate date,
            LocalTime timeFrom, LocalTime timeTo, int teacherId, int courseId, int groupId, int classRoomId) {
        teacherRepository.addTeacherToTheCourse(teacherId, "Mathematics");

        timeTableBuilder.saveGroupTimeTable(date, timeFrom, timeTo, teacherId, courseId, groupId, classRoomId);
        Group fakeGroup = new Group("fake-01", null);

        Exception noSuchElementException = assertThrows(Exception.class,
                () -> timeTableService.getGroupTimeTableByDate(date, date, fakeGroup));
        Assertions.assertEquals("Group not found", noSuchElementException.getMessage());
    }

    @ParameterizedTest
    @CsvSource({ "2023-09-01, 09:00, 10:30, 1, 1, 1, 1 " })
    void testGetAllTimeTablesByDate_ShouldReturnAllTimeTablesBetweenDates(LocalDate date, LocalTime timeFrom,
            LocalTime timeTo, int teacherId, int courseId, int groupId, int classRoomId) {
        TimeTable timeTable = new TimeTable();
        List<TimeTable> expectedTimeTableList = new ArrayList<>();

        IntStream.rangeClosed(1, 3).forEach(y -> {
            teacherRepository.addTeacherToTheCourse(y, "Mathematics");
            teacherRepository.addTeacherToTheCourse(y, "Physics");
            teacherRepository.addTeacherToTheCourse(y, "Chemistry");
        });

        for (int i = 0; i < 3; i++) {
            timeTable = timeTableBuilder.saveGroupTimeTable(date.plusDays(i), timeFrom, timeTo, teacherId++, courseId++,
                    groupId++, classRoomId++);
            expectedTimeTableList.add(timeTable);
        }

        Assertions.assertEquals(expectedTimeTableList, timeTableService.getAllTimeTablesByDate(date, date.plusDays(2)));
    }

    @ParameterizedTest
    @CsvSource({ "2023-09-01, 09:00, 10:30, 1, 1, 1, 1" })
    void testGetAllTimeTablesByDate_WhenDatesNotExists_ShouldReturnEmptyList(LocalDate date, LocalTime timeFrom,
            LocalTime timeTo, int teacherId, int courseId, int groupId, int classRoomId) {
        IntStream.rangeClosed(1, 3).forEach(y -> {
            teacherRepository.addTeacherToTheCourse(y, "Mathematics");
            teacherRepository.addTeacherToTheCourse(y, "Physics");
            teacherRepository.addTeacherToTheCourse(y, "Chemistry");
        });

        IntStream.range(0, 3).forEach(i -> timeTableBuilder.saveGroupTimeTable(date.plusDays(i), timeFrom, timeTo,
                teacherId + i, courseId + i, groupId + i, classRoomId + i));

        Assertions.assertTrue(timeTableService.getAllTimeTablesByDate(date.plusDays(30), date.plusDays(40)).isEmpty());
    }

    @ParameterizedTest
    @CsvSource({ "2023-09-01, 09:00, 10:30, 1, 1, 1, 1", "2023-09-01, 12:00, 13:30, 2, 2, 2, 2" })
    void testUpdateTimeTableById_ShouldReturnUpdatedTimeTable(LocalDate date, LocalTime timeFrom, LocalTime timeTo,
            int teacherId, int courseId, int groupId, int classRoomId) {
        teacherRepository.addTeacherToTheCourse(teacherId, "Mathematics");
        teacherRepository.addTeacherToTheCourse(teacherId, "Physics");
        teacherRepository.addTeacherToTheCourse(teacherId, "Chemistry");

        teacherRepository.addTeacherToTheCourse(teacherId + 1, "Mathematics");
        teacherRepository.addTeacherToTheCourse(teacherId + 1, "Physics");
        teacherRepository.addTeacherToTheCourse(teacherId + 1, "Chemistry");

        timeTableBuilder.saveGroupTimeTable(date, timeFrom, timeTo, teacherId, courseId, groupId, classRoomId);
        TimeTable expectedTimeTable = timeTableBuilder.createTimeTable(date, timeFrom, timeTo, teacherId + 1,
                courseId + 1, groupId + 1, classRoomId + 1);
        expectedTimeTable.setId(1);

        Assertions.assertEquals(expectedTimeTable, timeTableService.updateTimeTableById(1, expectedTimeTable));
    }

    @ParameterizedTest
    @CsvSource({ "2023-09-01, 09:00, 10:30, 1, 1, 1, 1" })
    void testUpdateTimeTableById_WhenIdNotFound_ShouldThrowNoSuchElementException(LocalDate date, LocalTime timeFrom,
            LocalTime timeTo, int teacherId, int courseId, int groupId, int classRoomId) {
        teacherRepository.addTeacherToTheCourse(teacherId, "Mathematics");

        TimeTable timeTable = timeTableBuilder.saveGroupTimeTable(date, timeFrom, timeTo, teacherId, courseId, groupId,
                classRoomId);
        TimeTable expectedTimeTable = new TimeTable(date.plusMonths(1), timeFrom.plusHours(1), timeTo.plusHours(1),
                timeTable.getTeacher(), timeTable.getCourse(), timeTable.getGroup(), timeTable.getClassRoom());
        expectedTimeTable.setId(1);

        Exception noSuchElementException = assertThrows(Exception.class,
                () -> timeTableService.updateTimeTableById(2, expectedTimeTable));
        Assertions.assertEquals("Time Table not found", noSuchElementException.getMessage());
    }

    @ParameterizedTest
    @CsvSource({ "2023-09-01, 09:00, 10:30, 1, 1, 1, 1" })
    void testUpdateTimeTableById_WhenValidationFailed_ShouldThrowTimeTableValidationException(LocalDate date,
            LocalTime timeFrom, LocalTime timeTo, int teacherId, int courseId, int groupId, int classRoomId) {
        teacherRepository.addTeacherToTheCourse(teacherId, "Mathematics");

        TimeTable timeTable = timeTableBuilder.saveGroupTimeTable(date, timeFrom, timeTo, teacherId, courseId, groupId,
                classRoomId);
        TimeTable expectedTimeTable = new TimeTable(date.plusMonths(1), timeFrom.plusHours(1), timeTo.plusHours(1),
                timeTable.getTeacher(), timeTable.getCourse(), timeTable.getGroup(), timeTable.getClassRoom());
        expectedTimeTable.setId(1);

        Exception noSuchElementException = assertThrows(Exception.class,
                () -> timeTableService.updateTimeTableById(1, expectedTimeTable));
        Assertions.assertEquals("Validation failed while creating TimeTable", noSuchElementException.getMessage());
    }

    @ParameterizedTest
    @CsvSource({ "2023-09-01, 09:00, 10:30, 1, 1, 1, 1", "2023-09-01, 12:00, 13:30, 2, 2, 2, 2",
            "2023-09-02, 09:00, 10:30, 3, 3, 3, 3", "2023-09-02, 12:00, 13:30, 1, 1, 2, 3" })
    void testDeleteTimeTableById_ShouldReturnTimeTableId(LocalDate date, LocalTime timeFrom, LocalTime timeTo,
            int teacherId, int courseId, int groupId, int classRoomId) {
        IntStream.rangeClosed(1, 3).forEach(y -> {
            teacherRepository.addTeacherToTheCourse(y, "Mathematics");
            teacherRepository.addTeacherToTheCourse(y, "Physics");
            teacherRepository.addTeacherToTheCourse(y, "Chemistry");
        });

        TimeTable timeTable = timeTableBuilder.saveGroupTimeTable(date, timeFrom, timeTo, teacherId, courseId, groupId,
                classRoomId);

        Assertions.assertEquals(timeTableRepository.findById(1).get(), timeTable);
        Assertions.assertEquals(1, timeTableService.deleteTimeTableById(timeTable.getId()));
        Assertions.assertTrue(timeTableRepository.findAll().isEmpty());
    }

    @ParameterizedTest
    @CsvSource({ "2023-09-01, 09:00, 10:30, 1, 1, 1, 1" })
    void testDeleteTimeTableById_WhenIdNotFound_ShouldThrowNoSuchElementException(LocalDate date, LocalTime timeFrom,
            LocalTime timeTo, int teacherId, int courseId, int groupId, int classRoomId) {
        teacherRepository.addTeacherToTheCourse(teacherId, "Mathematics");

        timeTableBuilder.saveGroupTimeTable(date, timeFrom, timeTo, teacherId, courseId, groupId, classRoomId);

        Exception noSuchElementException = assertThrows(Exception.class, () -> timeTableService.deleteTimeTableById(2));
        Assertions.assertEquals("Time Table not found", noSuchElementException.getMessage());
    }

}