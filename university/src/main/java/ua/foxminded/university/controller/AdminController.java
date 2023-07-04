package ua.foxminded.university.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

	@Autowired
	private TeacherController teacherController;

	@GetMapping("/admin/main")
	public String adminMain() {
		return "admin/main";
	}

	public String getTeacherList(Model model) {
		return teacherController.getTeacherList(model);
	}
}
