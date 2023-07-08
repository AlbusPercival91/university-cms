package ua.foxminded.university.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ua.foxminded.university.dao.entities.Teacher;
import ua.foxminded.university.dao.service.TeacherService;

@Controller
public class TeacherController {

	@Autowired
	private TeacherService teacherService;

	@GetMapping("/teachers/search")
	public String searchPanel() {
		return "teachers/search";
	}

	@GetMapping("/teachers/list")
	public String getAllTeachersList(Model model) {
		List<Teacher> teachers = teacherService.getAllTeachers();

		for (Teacher teacher : teachers) {
			teacher.getAdditionalCourses();
		}
		model.addAttribute("teachers", teachers);
		return "teachers/list";
	}

	@PostMapping("/teachers/search")
	public String searchTeachers(@RequestParam("searchType") String searchType, String courseName, Model model) {
		List<Teacher> teachers = teacherService.findTeachersRelatedToCourse(courseName);

		for (Teacher teacher : teachers) {
			teacher.getAdditionalCourses();
		}
		model.addAttribute("teachers", teachers);
		return "teachers/list";
	}

}
