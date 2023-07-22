package ua.foxminded.university.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import ua.foxminded.university.dao.entities.ClassRoom;
import ua.foxminded.university.dao.entities.Course;
import ua.foxminded.university.dao.entities.Teacher;
import ua.foxminded.university.dao.service.ClassRoomService;
import ua.foxminded.university.dao.service.CourseService;
import ua.foxminded.university.dao.service.TeacherService;

@Controller
public class AdminTimeTableController {

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
}
