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
	public String searchTeachers(@RequestParam("searchType") String searchType,
			@RequestParam(required = false) String courseName, @RequestParam(required = false) String facultyName,
			@RequestParam(required = false) Integer departmentId, @RequestParam(required = false) Integer facultyId,
			@RequestParam(required = false) Integer teacherId, @RequestParam(required = false) String name,
			@RequestParam(required = false) String familyName, Model model) {
		List<Teacher> teachers;

		switch (searchType) {
		case "course":
			teachers = teacherService.findTeachersRelatedToCourse(courseName);
			break;
		case "faculty":
			teachers = teacherService.findAllByFacultyName(facultyName);
			break;
		case "department":
			teachers = teacherService.findAllByDepartmentIdAndDepartmentFacultyId(departmentId, facultyId);
			break;
//		case "teacher":
//			teachers = teacherService.findTeacherById(teacherId);
//			break;
//		case "nameAndFamilyName":
//			teachers = teacherService.findTeacherByNameAndFamilyName(name, familyName);
//			break;
		default:
			return "error";
		}

		for (Teacher teacher : teachers) {
			teacher.getAdditionalCourses();
		}

		model.addAttribute("teachers", teachers);
		return "teachers/list";
	}

}
