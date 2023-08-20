package ua.foxminded.university.controller.admin;

import static org.mockito.Mockito.when;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ua.foxminded.university.dao.entities.ClassRoom;
import ua.foxminded.university.dao.entities.Course;
import ua.foxminded.university.dao.entities.Teacher;
import ua.foxminded.university.dao.entities.TimeTable;
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

	@Test
	void testCeateTimeTableForStudentsAtCourse_Success() throws Exception {
		LocalDate date = LocalDate.of(2023, 8, 19);
		LocalTime timeFrom = LocalTime.of(9, 0);
		LocalTime timeTo = LocalTime.of(11, 0);

		Teacher teacher = new Teacher();
		Course course = new Course();
		ClassRoom classRoom = new ClassRoom();

		TimeTable timetable = new TimeTable(date, timeFrom, timeTo, teacher, course, classRoom, null);

		when(teacherService.findTeacherById(1)).thenReturn(Optional.of(teacher));
		when(courseService.findCourseById(1)).thenReturn(Optional.of(course));
		when(classRoomService.findClassRoomById(1)).thenReturn(Optional.of(classRoom));
		when(timeTableService.createTimeTableForStudentsAtCourse(date, timeFrom, timeTo, teacher, course, classRoom))
				.thenReturn(timetable);

		mockMvc.perform(MockMvcRequestBuilders.post("/admin/timetable/course-timetable-form")
				.param("date", String.valueOf(date)).param("timeFrom", String.valueOf(timeFrom))
				.param("timeTo", String.valueOf(timeTo)).flashAttr("timetable", timetable))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"))
				.andExpect(MockMvcResultMatchers.redirectedUrl("/admin/timetable/course-timetable-form"));
	}

//	@Test
//	void testCeateTimeTableForStudentsAtCourse_Failure() throws Exception {
//		LocalDate date = LocalDate.now();
//		LocalTime timeFrom = LocalTime.of(9, 0);
//		LocalTime timeTo = LocalTime.of(11, 0);
//		TimeTable timetable = new TimeTable();
//
//		when(timeTableService.createTimeTableForStudentsAtCourse(eq(date), eq(timeFrom), eq(timeTo), any(Teacher.class),
//				any(Course.class), any(ClassRoom.class)))
//				.thenThrow(new TimeTableValidationException("Validation error"));
//
//		mockMvc.perform(MockMvcRequestBuilders.post("/admin/timetable/course-timetable-form")
//				.param("date", date.toString()).param("timeFrom", timeFrom.toString())
//				.param("timeTo", timeTo.toString()).flashAttr("timetable", timetable))
//				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
//				.andExpect(MockMvcResultMatchers.flash().attributeExists("errorMessage"))
//				.andExpect(MockMvcResultMatchers.redirectedUrl("/admin/timetable/course-timetable-form"));
//	}
}
