package ua.foxminded.university.controller;

import java.util.ArrayList;
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
	public String searchTeachers(@RequestParam("searchType") String searchType,
			@RequestParam(required = false) String courseName, @RequestParam(required = false) String facultyName,
			@RequestParam(required = false) Integer departmentId, @RequestParam(required = false) Integer facultyId,
			@RequestParam(required = false) Integer teacherId, @RequestParam(required = false) String firstName,
			@RequestParam(required = false) String familyName, Model model) {
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
		} else if ("nameAndFamilyName".equals(searchType)) {
			teachers = new ArrayList<>();
			teachers.add(teacherService.findTeacherByName(firstName, familyName));
		} else {
			return "error";
		}

		teachers.forEach(Teacher::getAdditionalCourses);
		model.addAttribute("teachers", teachers);
		return "teachers/list";
	}

}
