package ua.foxminded.university.controller;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ua.foxminded.university.dao.entities.ClassRoom;
import ua.foxminded.university.dao.entities.Course;
import ua.foxminded.university.dao.entities.Group;
import ua.foxminded.university.dao.entities.Student;
import ua.foxminded.university.dao.entities.Teacher;
import ua.foxminded.university.dao.entities.TimeTable;
import ua.foxminded.university.dao.exception.TimeTableValidationException;
import ua.foxminded.university.dao.service.ClassRoomService;
import ua.foxminded.university.dao.service.CourseService;
import ua.foxminded.university.dao.service.GroupService;
import ua.foxminded.university.dao.service.StudentService;
import ua.foxminded.university.dao.service.TeacherService;
import ua.foxminded.university.dao.service.TimeTableService;
import ua.foxminded.university.validation.IdCollector;
import ua.foxminded.university.validation.Message;

@WebMvcTest({ TimeTableController.class, IdCollector.class })
@ActiveProfiles("test-container")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class TimeTableControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TimeTableService timeTableService;

    @MockBean
    private CourseService courseService;

    @MockBean
    private GroupService groupService;

    @MockBean
    private TeacherService teacherService;

    @MockBean
    private ClassRoomService classRoomService;

    @MockBean
    private StudentService studentService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void testShowFormCreateTimeTableForStudentsAtCourse() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/timetable/course-timetable-form"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("timetable/course-timetable-form"));
    }

    @ParameterizedTest
    @CsvSource({ "2023-09-01, 09:00, 10:30" })
    @WithMockUser(roles = "ADMIN")
    void testCeateTimeTableForStudentsAtCourse_Success(LocalDate date, LocalTime timeFrom, LocalTime timeTo)
            throws Exception {
        TimeTable timetable = new TimeTable(date, timeFrom, timeTo, new Teacher(), new Course(), new ClassRoom(), null);

        when(timeTableService.createTimeTableForStudentsAtCourse(date, timeFrom, timeTo, new Teacher(), new Course(),
                new ClassRoom())).thenReturn(timetable);

        mockMvc.perform(MockMvcRequestBuilders.post("/timetable/course-timetable-form")
                .param("date", String.valueOf(date)).param("timeFrom", String.valueOf(timeFrom))
                .param("timeTo", String.valueOf(timeTo)).flashAttr("timetable", timetable).with(csrf().asHeader()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.flash().attributeExists(Message.SUCCESS))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/timetable/course-timetable-form"));
    }

    @ParameterizedTest
    @CsvSource({ "2023-09-01, 09:00, 10:30" })
    @WithMockUser(roles = "ADMIN")
    void testCeateTimeTableForStudentsAtCourse_Failure(LocalDate date, LocalTime timeFrom, LocalTime timeTo)
            throws Exception {
        TimeTable timetable = new TimeTable(date, timeFrom, timeTo, new Teacher(), new Course(), new ClassRoom(), null);

        when(timeTableService.createTimeTableForStudentsAtCourse(date, timeFrom, timeTo, new Teacher(), new Course(),
                new ClassRoom())).thenThrow(new TimeTableValidationException("Validation error"));

        mockMvc.perform(MockMvcRequestBuilders.post("/timetable/course-timetable-form")
                .param("date", String.valueOf(date)).param("timeFrom", String.valueOf(timeFrom))
                .param("timeTo", String.valueOf(timeTo)).flashAttr("timetable", timetable).with(csrf().asHeader()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.flash().attributeExists(Message.ERROR))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/timetable/course-timetable-form"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testShowFormCreateGroupTimeTable() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/timetable/group-timetable-form"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("timetable/group-timetable-form"));
    }

    @ParameterizedTest
    @CsvSource({ "2023-09-01, 09:00, 10:30" })
    @WithMockUser(roles = "ADMIN")
    void testCreateGroupTimeTable_Success(LocalDate date, LocalTime timeFrom, LocalTime timeTo) throws Exception {
        TimeTable timetable = new TimeTable(date, timeFrom, timeTo, new Teacher(), new Course(), new Group(),
                new ClassRoom());

        when(timeTableService.createGroupTimeTable(date, timeFrom, timeTo, new Teacher(), new Course(), new Group(),
                new ClassRoom())).thenReturn(timetable);

        mockMvc.perform(MockMvcRequestBuilders.post("/timetable/group-timetable-form")
                .param("date", String.valueOf(date)).param("timeFrom", String.valueOf(timeFrom))
                .param("timeTo", String.valueOf(timeTo)).flashAttr("timetable", timetable).with(csrf().asHeader()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.flash().attributeExists(Message.SUCCESS))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/timetable/group-timetable-form"));
    }

    @ParameterizedTest
    @CsvSource({ "2023-09-01, 09:00, 10:30" })
    @WithMockUser(roles = "ADMIN")
    void testCreateGroupTimeTable_Failure(LocalDate date, LocalTime timeFrom, LocalTime timeTo) throws Exception {
        TimeTable timetable = new TimeTable(date, timeFrom, timeTo, new Teacher(), new Course(), new Group(),
                new ClassRoom());

        when(timeTableService.createGroupTimeTable(date, timeFrom, timeTo, new Teacher(), new Course(), new Group(),
                new ClassRoom())).thenThrow(new TimeTableValidationException("Validation error"));

        mockMvc.perform(MockMvcRequestBuilders.post("/timetable/group-timetable-form")
                .param("date", String.valueOf(date)).param("timeFrom", String.valueOf(timeFrom))
                .param("timeTo", String.valueOf(timeTo)).flashAttr("timetable", timetable).with(csrf().asHeader()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.flash().attributeExists(Message.ERROR))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/timetable/group-timetable-form"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteTimetable() throws Exception {
        int timetableId = 1;
        mockMvc.perform(
                MockMvcRequestBuilders.post("/timetable/delete/{timetableId}", timetableId).with(csrf().asHeader()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.flash().attributeExists(Message.SUCCESS))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/timetable/timetable/" + timetableId));
    }

    @ParameterizedTest
    @CsvSource({ "2023-09-01, 09:00, 10:30" })
    @WithMockUser(roles = { "ADMIN", "TEACHER" })
    void testGetFullTeacherTimeTable(LocalDate date, LocalTime timeFrom, LocalTime timeTo) throws Exception {
        Teacher teacher = new Teacher();
        teacher.setId(1);

        TimeTable timetable1 = new TimeTable(date, timeFrom, timeTo, teacher, new Course(), new Group(),
                new ClassRoom());
        List<TimeTable> timetables = Collections.singletonList(timetable1);

        when(teacherService.findTeacherById(teacher.getId())).thenReturn(Optional.of(teacher));
        when(timeTableService.getTeacherTimeTable(teacher)).thenReturn(timetables);

        mockMvc.perform(MockMvcRequestBuilders.get("/timetable/teacher-timetable/{teacherId}", teacher.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("timetables"))
                .andExpect(MockMvcResultMatchers.view().name("timetable/timetable"));
    }

    @ParameterizedTest
    @CsvSource({ "2023-09-01, 2023-10-01, 09:00, 10:30" })
    @WithMockUser(roles = { "ADMIN", "TEACHER" })
    void testGetSelectedDateTeacherTimeTable(LocalDate date1, LocalDate date2, LocalTime timeFrom, LocalTime timeTo)
            throws Exception {
        Teacher teacher = new Teacher();
        teacher.setId(1);

        TimeTable timetable1 = new TimeTable(date1, timeFrom, timeTo, teacher, new Course(), new Group(),
                new ClassRoom());
        TimeTable timetable2 = new TimeTable(date2, timeFrom, timeTo, teacher, new Course(), new Group(),
                new ClassRoom());
        List<TimeTable> timetables = Arrays.asList(timetable1, timetable2);

        when(teacherService.findTeacherById(teacher.getId())).thenReturn(Optional.of(teacher));
        when(timeTableService.getTeacherTimeTableByDate(date1, date2, teacher)).thenReturn(timetables);

        mockMvc.perform(MockMvcRequestBuilders.get("/timetable/teacher-selected-timetable/{teacherId}", teacher.getId())
                .param("dateFrom", String.valueOf(date1)).param("dateTo", String.valueOf(date2)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("timetables"))
                .andExpect(MockMvcResultMatchers.view().name("timetable/timetable")).andExpect(MockMvcResultMatchers
                        .model().attribute("timetables", Matchers.contains(timetable1, timetable2)));
    }

    @ParameterizedTest
    @CsvSource({ "2023-09-01, 09:00, 10:30" })
    @WithMockUser(roles = { "ADMIN", "STUDENT" })
    void testGetFullStudentTimeTable(LocalDate date, LocalTime timeFrom, LocalTime timeTo) throws Exception {
        Student student = new Student();
        student.setId(1);

        TimeTable timetable1 = new TimeTable(date, timeFrom, timeTo, new Teacher(), new Course(), new Group(),
                new ClassRoom());
        List<TimeTable> timetables = Collections.singletonList(timetable1);

        when(studentService.findStudentById(student.getId())).thenReturn(Optional.of(student));
        when(timeTableService.getStudentTimeTable(student.getId())).thenReturn(timetables);

        mockMvc.perform(MockMvcRequestBuilders.get("/timetable/student-timetable/{studentId}", student.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("timetables"))
                .andExpect(MockMvcResultMatchers.view().name("timetable/timetable"));
    }

    @ParameterizedTest
    @CsvSource({ "2023-09-01, 09:00, 10:30" })
    @WithMockUser(roles = { "ADMIN", "STUDENT" })
    void testGetFullGroupTimeTable(LocalDate date, LocalTime timeFrom, LocalTime timeTo) throws Exception {
        Student student = new Student();
        student.setId(1);

        TimeTable timetable1 = new TimeTable(date, timeFrom, timeTo, new Teacher(), new Course(), new Group(),
                new ClassRoom());
        List<TimeTable> timetables = Collections.singletonList(timetable1);

        when(studentService.findStudentById(student.getId())).thenReturn(Optional.of(student));
        when(timeTableService.getStudentsGroupTimeTable(student)).thenReturn(timetables);

        mockMvc.perform(MockMvcRequestBuilders.get("/timetable/timetable-group/{studentId}", student.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("timetables"))
                .andExpect(MockMvcResultMatchers.view().name("timetable/timetable"));
    }

    @ParameterizedTest
    @CsvSource({ "1, 2023-09-01, 2023-10-01, 09:00, 10:30, selected-timetable" })
    @WithMockUser(roles = { "ADMIN", "STUDENT" })
    void testGetSelectedDateStudentAndGroupTimeTable_WhenOnActionIsStudent(int studentId, LocalDate date1,
            LocalDate date2, LocalTime timeFrom, LocalTime timeTo, String action) throws Exception {
        Student student = new Student();
        student.setId(studentId);

        TimeTable timetable1 = new TimeTable(date1, timeFrom, timeTo, new Teacher(), new Course(), new Group(),
                new ClassRoom());
        TimeTable timetable2 = new TimeTable(date2, timeFrom, timeTo, new Teacher(), new Course(), new Group(),
                new ClassRoom());
        List<TimeTable> timetables = Arrays.asList(timetable1, timetable2);

        when(studentService.findStudentById(studentId)).thenReturn(Optional.of(student));
        when(timeTableService.getStudentTimeTableByDate(date1, date2, studentId)).thenReturn(timetables);

        mockMvc.perform(MockMvcRequestBuilders.get("/timetable/selected-timetable/{studentId}", studentId)
                .param("dateFrom", String.valueOf(date1)).param("dateTo", String.valueOf(date2))
                .param("action", action)).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("timetables"))
                .andExpect(MockMvcResultMatchers.view().name("timetable/timetable"));
    }

    @ParameterizedTest
    @CsvSource({ "1, 2023-09-01, 2023-10-01, 09:00, 10:30, selected-group-timetable" })
    @WithMockUser(roles = { "ADMIN", "STUDENT" })
    void testGetSelectedDateStudentAndGroupTimeTable_WhenOnActionIsStudentsGroup(int studentId, LocalDate date1,
            LocalDate date2, LocalTime timeFrom, LocalTime timeTo, String action) throws Exception {
        Student student = new Student();
        student.setId(studentId);

        TimeTable timetable1 = new TimeTable(date1, timeFrom, timeTo, new Teacher(), new Course(), new Group(),
                new ClassRoom());
        TimeTable timetable2 = new TimeTable(date2, timeFrom, timeTo, new Teacher(), new Course(), new Group(),
                new ClassRoom());
        List<TimeTable> timetables = Arrays.asList(timetable1, timetable2);

        when(studentService.findStudentById(studentId)).thenReturn(Optional.of(student));
        when(timeTableService.getStudentsGroupTimeTableByDate(date1, date2, student)).thenReturn(timetables);

        mockMvc.perform(MockMvcRequestBuilders.get("/timetable/selected-timetable/{studentId}", studentId)
                .param("dateFrom", String.valueOf(date1)).param("dateTo", String.valueOf(date2))
                .param("action", action)).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("timetables"))
                .andExpect(MockMvcResultMatchers.view().name("timetable/timetable"));
    }

    @Test
    @WithMockUser(roles = { "ADMIN", "STUDENT", "TEACHER", "STAFF" })
    void testGetAllTimeTableList() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/timetable/timetable-list"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("timetables"))
                .andExpect(MockMvcResultMatchers.view().name("timetable/timetable"));
    }

    @ParameterizedTest
    @CsvSource({ "2023-09-01, 09:00, 10:30" })
    @WithMockUser(roles = "ADMIN")
    void testOpenTimeTableCard_WhenTimeTableExists(LocalDate date, LocalTime timeFrom, LocalTime timeTo)
            throws Exception {
        TimeTable timetable = new TimeTable(date, timeFrom, timeTo, new Teacher(), new Course(), new Group(),
                new ClassRoom());
        timetable.setId(1);

        when(timeTableService.findTimeTableById(timetable.getId())).thenReturn(Optional.of(timetable));

        mockMvc.perform(MockMvcRequestBuilders.get("/timetable/timetable-card/{timetableId}", timetable.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("timetable/timetable-card"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("timetable"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("teachers"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("courses"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("groups"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("classrooms"))
                .andExpect(MockMvcResultMatchers.model().attribute("timetable", Matchers.sameInstance(timetable)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testOpenTimeTableCard_WhenTimeTableDoesNotExist() throws Exception {
        int timetableId = 999;

        when(timeTableService.findTimeTableById(timetableId)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/timetable/timetable-card/{timetableId}", timetableId))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/timetable/timetable"));
    }

    @ParameterizedTest
    @CsvSource({ "2023-09-01, 09:00, 10:30" })
    @WithMockUser(roles = { "ADMIN", "STUDENT", "TEACHER", "STAFF" })
    void testSearchTimeTables_WhenSearchTypeIsTimeTable(LocalDate date, LocalTime timeFrom, LocalTime timeTo)
            throws Exception {
        TimeTable timetable = new TimeTable(date, timeFrom, timeTo, new Teacher(), new Course(), new Group(),
                new ClassRoom());
        timetable.setId(1);

        when(timeTableService.findTimeTableById(timetable.getId())).thenReturn(Optional.of(timetable));

        mockMvc.perform(MockMvcRequestBuilders.get("/timetable/search-result").param("searchType", "timetable")
                .param("timetableId", String.valueOf(timetable.getId())))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("timetable/timetable"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("timetables"))
                .andExpect(MockMvcResultMatchers.model().attribute("timetables", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.model().attribute("timetables", Matchers.contains(timetable)));
    }

    @ParameterizedTest
    @CsvSource({ "2023-09-01, 09:00, 10:30, Math" })
    @WithMockUser(roles = { "ADMIN", "STUDENT", "TEACHER", "STAFF" })
    void testSearchTimeTables_WhenSearchTypeIsCourseName(LocalDate date, LocalTime timeFrom, LocalTime timeTo,
            String courseName) throws Exception {
        TimeTable timetable = new TimeTable(date, timeFrom, timeTo, new Teacher(), new Course(), new Group(),
                new ClassRoom());

        when(timeTableService.findTimeTableByCourseName(courseName)).thenReturn(Collections.singletonList(timetable));

        mockMvc.perform(MockMvcRequestBuilders.get("/timetable/search-result").param("searchType", "course")
                .param("courseName", courseName)).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("timetable/timetable"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("timetables"))
                .andExpect(MockMvcResultMatchers.model().attribute("timetables", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.model().attribute("timetables", Matchers.contains(timetable)));
    }

    @ParameterizedTest
    @CsvSource({ "2023-09-01, 09:00, 10:30, Math" })
    @WithMockUser(roles = { "ADMIN", "STUDENT", "TEACHER", "STAFF" })
    void testSearchTimeTables_WhenSearchTypeIsGroupName(LocalDate date, LocalTime timeFrom, LocalTime timeTo,
            String groupName) throws Exception {
        TimeTable timetable = new TimeTable(date, timeFrom, timeTo, new Teacher(), new Course(), new Group(),
                new ClassRoom());

        when(timeTableService.findTimeTableByGroupName(groupName)).thenReturn(Collections.singletonList(timetable));

        mockMvc.perform(MockMvcRequestBuilders.get("/timetable/search-result").param("searchType", "group")
                .param("groupName", groupName)).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("timetable/timetable"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("timetables"))
                .andExpect(MockMvcResultMatchers.model().attribute("timetables", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.model().attribute("timetables", Matchers.contains(timetable)));
    }

    @ParameterizedTest
    @CsvSource({ "2023-09-01, 09:00, 10:30, Math" })
    @WithMockUser(roles = { "ADMIN", "STUDENT", "TEACHER", "STAFF" })
    void testSearchTimeTables_WhenSearchTypeIsDate(LocalDate date, LocalTime timeFrom, LocalTime timeTo)
            throws Exception {
        TimeTable timetable = new TimeTable(date, timeFrom, timeTo, new Teacher(), new Course(), new Group(),
                new ClassRoom());

        when(timeTableService.findTimeTablesByDate(date, date)).thenReturn(Collections.singletonList(timetable));

        mockMvc.perform(MockMvcRequestBuilders.get("/timetable/search-result").param("searchType", "date")
                .param("dateFrom", String.valueOf(date)).param("dateTo", String.valueOf(date)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("timetable/timetable"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("timetables"))
                .andExpect(MockMvcResultMatchers.model().attribute("timetables", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.model().attribute("timetables", Matchers.contains(timetable)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateTimeTable_Success() throws Exception {
        int timeTableId = 1;
        TimeTable updatedTimeTable = new TimeTable();
        updatedTimeTable.setId(timeTableId);

        when(timeTableService.updateTimeTableById(timeTableId, updatedTimeTable)).thenReturn(updatedTimeTable);

        mockMvc.perform(MockMvcRequestBuilders.post("/timetable/edit-timetable/{timetableId}", timeTableId)
                .flashAttr("timetable", updatedTimeTable).with(csrf().asHeader()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.flash().attributeExists(Message.SUCCESS))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/timetable/timetable-card/" + timeTableId));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateTimeTable_Failure() throws Exception {
        int timeTableId = 1;
        TimeTable updatedTimeTable = new TimeTable();
        updatedTimeTable.setId(timeTableId);

        when(timeTableService.updateTimeTableById(timeTableId, updatedTimeTable)).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.post("/timetable/edit-timetable/{timetableId}", timeTableId)
                .flashAttr("timetable", updatedTimeTable).with(csrf().asHeader()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.flash().attributeExists(Message.ERROR))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/timetable/timetable-card/" + timeTableId));
    }
}
