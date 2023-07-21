package ua.foxminded.university.controller;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import ua.foxminded.university.dao.entities.Course;
import ua.foxminded.university.dao.entities.Student;
import ua.foxminded.university.dao.service.CourseService;
import ua.foxminded.university.dao.service.StudentService;

@Controller
public class AdminStudentController {

	@Autowired
	private StudentService studentService;

	@Autowired
	private CourseService courseService;

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

	@GetMapping("/admin/student/student-card/{studentId}")
	public String openStudentCard(@PathVariable int studentId, Model model) {
		Optional<Student> optionalStudent = studentService.findStudentById(studentId);
		List<Course> courses = courseService.getAllCourses();

		if (optionalStudent.isPresent()) {
			Student student = optionalStudent.get();
			model.addAttribute("student", student);
			model.addAttribute("courses", courses);
			return "admin/student/student-card";
		} else {
			return "redirect:/admin/student/edit-student-list";
		}
	}

	@PostMapping("/admin/student/remove-course/{studentId}/{courseName}")
	public String removeStudentFromCourse(@PathVariable int studentId, @PathVariable String courseName,
			RedirectAttributes redirectAttributes) {
		try {
			studentService.removeStudentFromCourse(studentId, courseName);
			redirectAttributes.addFlashAttribute("successMessage", "Student unsubscribed from Course!");
		} catch (IllegalStateException | NoSuchElementException ex) {
			redirectAttributes.addFlashAttribute("errorMessage", ex.getLocalizedMessage());
		}
		return "redirect:/admin/student/student-card/{studentId}";
	}

	@PostMapping("/admin/student/assign-course")
	public String addStudentToTheCourse(@RequestParam int studentId, @RequestParam String courseName,
			RedirectAttributes redirectAttributes) {
		try {
			studentService.addStudentToTheCourse(studentId, courseName);
			redirectAttributes.addFlashAttribute("successMessage", "Student subscribed to Course!");
		} catch (IllegalStateException | NoSuchElementException ex) {
			redirectAttributes.addFlashAttribute("errorMessage", ex.getLocalizedMessage());
		}
		UriComponentsBuilder builder = UriComponentsBuilder.fromPath("/admin/student/student-card/{studentId}");
		return "redirect:" + builder.buildAndExpand(studentId).toUriString();
	}

}
