package ua.foxminded.university.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ua.foxminded.university.dao.entities.Student;
import ua.foxminded.university.dao.service.StudentService;

@Controller
public class AdminStudentController {

	@Autowired
	private StudentService studentService;

	@GetMapping("/admin/student/edit-student-list")
	public String getAllStudentsListAsAdmin(Model model) {
		List<Student> students = studentService.getAllStudents();

		for (Student student : students) {
			student.getCourses();
		}
		model.addAttribute("students", students);
		return "admin/student/edit-student-list";
	}
}
