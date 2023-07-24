package ua.foxminded.university.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ua.foxminded.university.dao.entities.ClassRoom;
import ua.foxminded.university.dao.entities.Course;
import ua.foxminded.university.dao.entities.Student;
import ua.foxminded.university.dao.entities.Teacher;
import ua.foxminded.university.dao.entities.TimeTable;
import ua.foxminded.university.dao.exception.TimeTableValidationException;
import ua.foxminded.university.dao.service.ClassRoomService;
import ua.foxminded.university.dao.service.CourseService;
import ua.foxminded.university.dao.service.StudentService;
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

	@Autowired
	private StudentService studentService;

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
			BindingResult bindingResult, RedirectAttributes redirectAttributes,
			@RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
			@RequestParam("timeFrom") @DateTimeFormat(pattern = "HH:mm") LocalTime timeFrom,
			@RequestParam("timeTo") @DateTimeFormat(pattern = "HH:mm") LocalTime timeTo) {
		try {
			TimeTable createdTimeTable = timeTableService.createTimeTableForStudentsAtCourse(date, timeFrom, timeTo,
					timetable.getTeacher(), timetable.getCourse(), timetable.getClassRoom());

			if (!createdTimeTable.getDate().equals(date) && !createdTimeTable.getTimeFrom().equals(timeTo)
					&& !createdTimeTable.getTimeTo().equals(timeTo)) {
				redirectAttributes.addFlashAttribute("errorMessage", "Failed to create Time Table");
			} else {
				redirectAttributes.addFlashAttribute("successMessage", "Time Table created successfully");
			}
		} catch (NoSuchElementException | TimeTableValidationException ex) {
			redirectAttributes.addFlashAttribute("errorMessage", ex.getLocalizedMessage());
		}
		return "redirect:/admin/timetable/course-timetable-form";
	}

	@PostMapping("/admin/timetable/delete/{timetableId}")
	public String deleteTimetable(@PathVariable int timetableId, RedirectAttributes redirectAttributes,
			HttpServletRequest request) {
		try {
			timeTableService.deleteTimeTableById(timetableId);
			redirectAttributes.addFlashAttribute("successMessage", "Time Table was deleted!");
		} catch (NoSuchElementException ex) {
			redirectAttributes.addFlashAttribute("errorMessage", ex.getLocalizedMessage());
		}
		String referrer = request.getHeader("referer");

		if (referrer == null || referrer.isEmpty()) {
			return "redirect:/admin/timetable/timetable/{teacherId}";
		}
		return "redirect:" + referrer;
	}

	@GetMapping("/admin/timetable/timetable/{teacherId}")
	public String getFullTeacherTimeTable(@PathVariable("teacherId") int teacherId, Model model) {
		Optional<Teacher> teacher = teacherService.findTeacherById(teacherId);

		if (teacher.isPresent()) {
			List<TimeTable> timetables = timeTableService.getTeacherTimeTable(teacher.get());

			for (TimeTable timetable : timetables) {
				List<Student> studentsRelatedToCourse = studentService
						.findStudentsRelatedToCourse(timetable.getCourse().getCourseName());
				timetable.setStudentsRelatedToCourse(studentsRelatedToCourse);
			}

			model.addAttribute("timetables", timetables);
			return "admin/timetable/timetable";
		}
		return "admin/timetable/timetable";
	}
}
