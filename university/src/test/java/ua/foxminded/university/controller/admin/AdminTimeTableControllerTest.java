package ua.foxminded.university.controller.admin;

import static org.mockito.Mockito.when;
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
		int timetableId = 1;
		mockMvc.perform(MockMvcRequestBuilders.post("/admin/timetable/delete/{timetableId}", timetableId))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"))
				.andExpect(MockMvcResultMatchers.redirectedUrl("/admin/timetable/timetable/" + timetableId));
	}

	@ParameterizedTest
	@CsvSource({ "2023-09-01, 09:00, 10:30" })
	void testGetFullTeacherTimeTable(LocalDate date, LocalTime timeFrom, LocalTime timeTo) throws Exception {
		Teacher teacher = new Teacher();
		teacher.setId(1);

		TimeTable timetable1 = new TimeTable(date, timeFrom, timeTo, teacher, new Course(), new Group(),
				new ClassRoom());
		List<TimeTable> timetables = Collections.singletonList(timetable1);

		when(teacherService.findTeacherById(teacher.getId())).thenReturn(Optional.of(teacher));
		when(timeTableService.getTeacherTimeTable(teacher)).thenReturn(timetables);

		mockMvc.perform(MockMvcRequestBuilders.get("/admin/timetable/teacher-timetable/{teacherId}", teacher.getId()))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.model().attributeExists("timetables"))
				.andExpect(MockMvcResultMatchers.view().name("admin/timetable/timetable"));
	}

	@ParameterizedTest
	@CsvSource({ "2023-09-01, 2023-10-01, 09:00, 10:30" })
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

		mockMvc.perform(
				MockMvcRequestBuilders.get("/admin/timetable/teacher-selected-timetable/{teacherId}", teacher.getId())
						.param("dateFrom", String.valueOf(date1)).param("dateTo", String.valueOf(date2)))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.model().attributeExists("timetables"))
				.andExpect(MockMvcResultMatchers.view().name("admin/timetable/timetable"))
				.andExpect(MockMvcResultMatchers.model().attribute("timetables",
						Matchers.contains(timetable1, timetable2)));
	}
}
