package ua.foxminded.university.controller.user;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ua.foxminded.university.dao.entities.Course;
import ua.foxminded.university.dao.entities.Group;
import ua.foxminded.university.dao.entities.Student;
import ua.foxminded.university.dao.service.CourseService;
import ua.foxminded.university.dao.service.GroupService;
import ua.foxminded.university.dao.service.StudentService;
import ua.foxminded.university.validation.ControllerBindingValidator;

@Controller
public class StudentController {

	@Autowired
	private StudentService studentService;

	@Autowired
	private CourseService courseService;

	@Autowired
	private GroupService groupService;

	@Autowired
	private ControllerBindingValidator bindingValidator;

	@GetMapping("/user/student/main")
	public String studentMainPage(Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.isAuthenticated()) {
			String email = authentication.getName();
			Optional<Student> student = studentService.findStudentByEmail(email);

			if (student.isPresent()) {
				List<Course> courses = courseService.getAllCourses();
				List<Group> groups = groupService.getAllGroups();
				model.addAttribute("student", student.get());
				model.addAttribute("courses", courses);
				model.addAttribute("groups", groups);
				return "user/student/main";
			}
		}
		return "redirect:/login";
	}

	@PostMapping("/user/student/update-personal/{studentId}")
	public String updatePersonalData(@PathVariable("studentId") int studentId,
			@ModelAttribute("student") @Validated Student updatedStudent, BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {
		if (bindingValidator.validate(bindingResult, redirectAttributes)) {
			try {
				Student resultStudent = studentService.updateStudentById(studentId, updatedStudent);

				if (resultStudent != null) {
					redirectAttributes.addFlashAttribute("successMessage", "Data updated successfully");
				} else {
					redirectAttributes.addFlashAttribute("errorMessage", "Failed to update Data");
				}
			} catch (NoSuchElementException | IllegalStateException ex) {
				redirectAttributes.addFlashAttribute("errorMessage", ex.getLocalizedMessage());
			}
		} else {
			return "redirect:/user/student/main";
		}
		return "redirect:/user/student/main";
	}

	@PostMapping("/user/student/update-password")
	public String updatePassword(@RequestParam int studentId, @RequestParam String oldPassword,
			@RequestParam String newPassword, RedirectAttributes redirectAttributes) {
		try {
			Student resultStudent = studentService.changeStudentPasswordById(studentId, oldPassword, newPassword);
			if (resultStudent != null) {
				redirectAttributes.addFlashAttribute("successMessage", "Password changed successfully");
			} else {
				redirectAttributes.addFlashAttribute("errorMessage", "Failed to change Password");
			}
		} catch (NoSuchElementException | IllegalStateException ex) {
			redirectAttributes.addFlashAttribute("errorMessage", ex.getLocalizedMessage());
		}
		return "redirect:/user/student/main";
	}

	@GetMapping("/user/student-list")
	public String getAllStudentsList(Model model) {
		List<Student> students = studentService.getAllStudents();

		for (Student student : students) {
			student.getCourses();
		}
		model.addAttribute("students", students);
		return "user/student-list";
	}

	@GetMapping("/user/student/search-result")
	public String searchStudent(@RequestParam("searchType") String searchType,
			@RequestParam(required = false) String courseName, @RequestParam(required = false) String facultyName,
			@RequestParam(required = false) String groupName, @RequestParam(required = false) Integer facultyId,
			@RequestParam(required = false) Integer studentId, @RequestParam(required = false) String firstName,
			@RequestParam(required = false) String lastName, Model model) {
		List<Student> students;

		if ("course".equals(searchType)) {
			students = studentService.findStudentsRelatedToCourse(courseName);
		} else if ("faculty".equals(searchType)) {
			students = studentService.findAllByGroupFacultyFacultyName(facultyName);
		} else if ("group".equals(searchType)) {
			students = studentService.findAllByGroupName(groupName);
		} else if ("student".equals(searchType)) {
			Optional<Student> optionalStudent = studentService.findStudentById(studentId);
			students = optionalStudent.map(Collections::singletonList).orElse(Collections.emptyList());
		} else if ("firstNameAndLastName".equals(searchType)) {
			students = studentService.findStudentByName(firstName, lastName);
		} else {
			return "error";
		}
		students.forEach(Student::getCourses);
		model.addAttribute("students", students);
		return "user/student-list";
	}

}
