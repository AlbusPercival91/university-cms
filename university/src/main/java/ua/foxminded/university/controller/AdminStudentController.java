package ua.foxminded.university.controller;

import java.util.List;
import java.util.NoSuchElementException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

	@PostMapping("/admin/student/delete/{studentId}")
	public String deleteStudent(@PathVariable int studentId, RedirectAttributes redirectAttributes,
			HttpServletRequest request) {
		try {
			studentService.deleteStudentById(studentId);
			redirectAttributes.addFlashAttribute("successMessage", "Student was deleted!");
		} catch (NoSuchElementException ex) {
			redirectAttributes.addFlashAttribute("errorMessage", ex.getLocalizedMessage());
		}
		String referrer = request.getHeader("referer");

		if (referrer == null || referrer.isEmpty()) {
			return "redirect:/admin/student/edit-student-list";
		}
		return "redirect:" + referrer;
	}
}
