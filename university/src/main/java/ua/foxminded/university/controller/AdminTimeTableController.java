package ua.foxminded.university.controller;

import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ua.foxminded.university.dao.entities.ClassRoom;
import ua.foxminded.university.dao.entities.Course;
import ua.foxminded.university.dao.entities.Teacher;
import ua.foxminded.university.dao.entities.TimeTable;
import ua.foxminded.university.dao.service.ClassRoomService;
import ua.foxminded.university.dao.service.CourseService;
import ua.foxminded.university.dao.service.TeacherService;
import ua.foxminded.university.dao.service.TimeTableService;

@Controller
public class AdminTimeTableController {

	@Autowired
	private TimeTableService timeTableService;

	@Autowired
	private CourseService courseService;

	@Autowired
	private TeacherService teacherService;

	@Autowired
	private ClassRoomService classRoomService;

	@GetMapping("/admin/timetable/course-timetable-form")
	public String showFormCreateTimeTableForStudentsAtCourse(Model model) {
		List<Teacher> teachers = teacherService.getAllTeachers();
		List<Course> courses = courseService.getAllCourses();
		List<ClassRoom> classrooms = classRoomService.getAllClassRooms();
		model.addAttribute("teachers", teachers);
		model.addAttribute("courses", courses);
		model.addAttribute("classrooms", classrooms);
		return "admin/timetable/course-timetable-form";
	}

	@PostMapping("/admin/timetable/course-timetable-form")
	public String ceateTimeTableForStudentsAtCourse(@ModelAttribute("timetable") @Validated TimeTable timetable,
			BindingResult bindingResult, RedirectAttributes redirectAttributes) {
		try {

			TimeTable createdTimeTable = timeTableService.createTimeTableForStudentsAtCourse(timetable.getDate(),
					timetable.getTimeFrom(), timetable.getTimeTo(), timetable.getTeacher(), timetable.getCourse(),
					timetable.getClassRoom());

			if (createdTimeTable.getId() != timetable.getId()) {
				redirectAttributes.addFlashAttribute("errorMessage", "Failed to create Time Table");
			} else {
				redirectAttributes.addFlashAttribute("successMessage", "Time Table created successfully");
			}
		} catch (NoSuchElementException | IllegalStateException ex) {
			redirectAttributes.addFlashAttribute("errorMessage", ex.getLocalizedMessage());
		}
		return "redirect:/admin/timetable/course-timetable-form";
	}
}
