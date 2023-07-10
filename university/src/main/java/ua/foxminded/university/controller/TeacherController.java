package ua.foxminded.university.controller;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ua.foxminded.university.dao.entities.Course;
import ua.foxminded.university.dao.entities.Department;
import ua.foxminded.university.dao.entities.Teacher;
import ua.foxminded.university.dao.service.CourseService;
import ua.foxminded.university.dao.service.DepartmentService;
import ua.foxminded.university.dao.service.TeacherService;

@Controller
public class TeacherController {

	@Autowired
	private TeacherService teacherService;

	@Autowired
	private DepartmentService departmentService;

	@Autowired
	private CourseService courseService;

	@GetMapping("/teachers/teacher-search")
	public String searchPanel() {
		return "teachers/teacher-search";
	}

	@PostMapping("/teachers/create-teacher")
	public String createTeacher(@ModelAttribute("teacher") Teacher teacher, Model model) {
		if (teacher != null) {
			teacherService.createAndAssignTeacherToCourse(teacher);
			return "redirect:/teachers/create-teacher"; // Redirect to the same page after creating the teacher
		}

		List<Department> departments = departmentService.getAllDepartments();
		List<Course> courses = courseService.getAllCourses();
		model.addAttribute("departments", departments);
		model.addAttribute("courses", courses);
		return "teachers/create-teacher";
	}

	@GetMapping("/teachers/teacher-card")
	public String openTeacherCard() {
		return "teachers/teacher-card";
	}

	@GetMapping("/teachers/teacher-list")
	public String getAllTeachersList(Model model) {
		List<Teacher> teachers = teacherService.getAllTeachers();

		for (Teacher teacher : teachers) {
			teacher.getAdditionalCourses();
		}
		model.addAttribute("teachers", teachers);
		return "teachers/teacher-list";
	}

	@PostMapping("/teachers/teacher-search")
	public String searchTeachers(@RequestParam("searchType") String searchType,
			@RequestParam(required = false) String courseName, @RequestParam(required = false) String facultyName,
			@RequestParam(required = false) Integer departmentId, @RequestParam(required = false) Integer facultyId,
			@RequestParam(required = false) Integer teacherId, @RequestParam(required = false) String firstName,
			@RequestParam(required = false) String lastName, Model model) {
		List<Teacher> teachers;

		if ("course".equals(searchType)) {
			teachers = teacherService.findTeachersRelatedToCourse(courseName);
		} else if ("faculty".equals(searchType)) {
			teachers = teacherService.findAllByFacultyName(facultyName);
		} else if ("department".equals(searchType)) {
			teachers = teacherService.findAllByDepartmentIdAndDepartmentFacultyId(departmentId, facultyId);
		} else if ("teacher".equals(searchType)) {
			teachers = new ArrayList<>();
			teachers.add(teacherService.findTeacherById(teacherId));
		} else if ("firstNameAndLastName".equals(searchType)) {
			teachers = new ArrayList<>();
			teachers.add(teacherService.findTeacherByName(firstName, lastName));
		} else {
			return "error";
		}

		teachers.forEach(Teacher::getAdditionalCourses);
		model.addAttribute("teachers", teachers);
		return "teachers/teacher-list";
	}

}
