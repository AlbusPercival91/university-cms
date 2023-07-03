package ua.foxminded.university.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ua.foxminded.university.dao.service.TeacherService;

@Controller
public class AdminController {

	@Autowired
	private TeacherService teacherService;

	@GetMapping("/teacher")
	public String listAllTeachers(Model model) {
		model.addAttribute("teachers", teacherService.getAllTeachers());
		return "teachers/list";
	}

}
