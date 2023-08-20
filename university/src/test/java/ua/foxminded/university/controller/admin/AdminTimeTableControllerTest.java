package ua.foxminded.university.controller.admin;

import static org.mockito.Mockito.when;
import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ua.foxminded.university.dao.entities.ClassRoom;
import ua.foxminded.university.dao.entities.Course;
import ua.foxminded.university.dao.entities.Group;
import ua.foxminded.university.dao.entities.Teacher;
import ua.foxminded.university.dao.entities.TimeTable;
import ua.foxminded.university.dao.exception.TimeTableValidationException;
import ua.foxminded.university.dao.service.ClassRoomService;
import ua.foxminded.university.dao.service.CourseService;
import ua.foxminded.university.dao.service.GroupService;
import ua.foxminded.university.dao.service.StudentService;
import ua.foxminded.university.dao.service.TeacherService;
import ua.foxminded.university.dao.service.TimeTableService;

@WebMvcTest(AdminTimeTableController.class)
@ActiveProfiles("test-container")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class AdminTimeTableControllerTest {

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
	void testShowFormCreateTimeTableForStudentsAtCourse() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/admin/timetable/course-timetable-form"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("admin/timetable/course-timetable-form"));
	}

	@ParameterizedTest
	@CsvSource({ "2023-09-01, 09:00, 10:30" })
	void testCeateTimeTableForStudentsAtCourse_Success(LocalDate date, LocalTime timeFrom, LocalTime timeTo)
			throws Exception {
		TimeTable timetable = new TimeTable(date, timeFrom, timeTo, new Teacher(), new Course(), new ClassRoom(), null);

		when(timeTableService.createTimeTableForStudentsAtCourse(date, timeFrom, timeTo, new Teacher(), new Course(),
				new ClassRoom())).thenReturn(timetable);

		mockMvc.perform(MockMvcRequestBuilders.post("/admin/timetable/course-timetable-form")
				.param("date", String.valueOf(date)).param("timeFrom", String.valueOf(timeFrom))
				.param("timeTo", String.valueOf(timeTo)).flashAttr("timetable", timetable))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"))
				.andExpect(MockMvcResultMatchers.redirectedUrl("/admin/timetable/course-timetable-form"));
	}

	@ParameterizedTest
	@CsvSource({ "2023-09-01, 09:00, 10:30" })
	void testCeateTimeTableForStudentsAtCourse_Failure(LocalDate date, LocalTime timeFrom, LocalTime timeTo)
			throws Exception {
		TimeTable timetable = new TimeTable(date, timeFrom, timeTo, new Teacher(), new Course(), new ClassRoom(), null);

		when(timeTableService.createTimeTableForStudentsAtCourse(date, timeFrom, timeTo, new Teacher(), new Course(),
				new ClassRoom())).thenThrow(new TimeTableValidationException("Validation error"));

		mockMvc.perform(MockMvcRequestBuilders.post("/admin/timetable/course-timetable-form")
				.param("date", String.valueOf(date)).param("timeFrom", String.valueOf(timeFrom))
				.param("timeTo", String.valueOf(timeTo)).flashAttr("timetable", timetable))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.flash().attributeExists("errorMessage"))
				.andExpect(MockMvcResultMatchers.redirectedUrl("/admin/timetable/course-timetable-form"));
	}

	@Test
	void testShowFormCreateGroupTimeTable() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/admin/timetable/group-timetable-form"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("admin/timetable/group-timetable-form"));
	}

	@ParameterizedTest
	@CsvSource({ "2023-09-01, 09:00, 10:30" })
	void testCreateGroupTimeTable_Success(LocalDate date, LocalTime timeFrom, LocalTime timeTo) throws Exception {
		TimeTable timetable = new TimeTable(date, timeFrom, timeTo, new Teacher(), new Course(), new Group(),
				new ClassRoom());

		when(timeTableService.createGroupTimeTable(date, timeFrom, timeTo, new Teacher(), new Course(), new Group(),
				new ClassRoom())).thenReturn(timetable);

		mockMvc.perform(MockMvcRequestBuilders.post("/admin/timetable/group-timetable-form")
				.param("date", String.valueOf(date)).param("timeFrom", String.valueOf(timeFrom))
				.param("timeTo", String.valueOf(timeTo)).flashAttr("timetable", timetable))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"))
				.andExpect(MockMvcResultMatchers.redirectedUrl("/admin/timetable/group-timetable-form"));
	}

	@ParameterizedTest
	@CsvSource({ "2023-09-01, 09:00, 10:30" })
	void testCreateGroupTimeTable_Failure(LocalDate date, LocalTime timeFrom, LocalTime timeTo) throws Exception {
		TimeTable timetable = new TimeTable(date, timeFrom, timeTo, new Teacher(), new Course(), new Group(),
				new ClassRoom());

		when(timeTableService.createGroupTimeTable(date, timeFrom, timeTo, new Teacher(), new Course(), new Group(),
				new ClassRoom())).thenThrow(new TimeTableValidationException("Validation error"));

		mockMvc.perform(MockMvcRequestBuilders.post("/admin/timetable/group-timetable-form")
				.param("date", String.valueOf(date)).param("timeFrom", String.valueOf(timeFrom))
				.param("timeTo", String.valueOf(timeTo)).flashAttr("timetable", timetable))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.flash().attributeExists("errorMessage"))
				.andExpect(MockMvcResultMatchers.redirectedUrl("/admin/timetable/group-timetable-form"));
	}

	@Test
	void testDeleteTimetable() throws Exception {
		int teacherId = 1;
		mockMvc.perform(MockMvcRequestBuilders.post("/admin/timetable/delete/{timetableId}", 1))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"))
				.andExpect(MockMvcResultMatchers.redirectedUrl("/admin/timetable/timetable/" + teacherId));
	}
}
