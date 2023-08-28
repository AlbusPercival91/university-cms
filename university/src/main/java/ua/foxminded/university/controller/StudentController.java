package ua.foxminded.university.controller;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
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
import org.springframework.web.util.UriComponentsBuilder;
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

	@RolesAllowed("STUDENT")
	@GetMapping("/student/main")
	public String studentDashboard(Model model) {
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
				return "student/main";
			}
		}
		return "redirect:/login";
	}

	@RolesAllowed("STUDENT")
	@PostMapping("/student/update-personal/{studentId}")
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
			return "redirect:/student/main";
		}
		return "redirect:/student/main";
	}

	@RolesAllowed("STUDENT")
	@PostMapping("/student/update-password")
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
		return "redirect:/student/main";
	}

	@GetMapping("/student/student-list")
	public String getAllStudentsList(Model model) {
		List<Student> students = studentService.getAllStudents();

		for (Student student : students) {
			student.getCourses();
		}
		model.addAttribute("students", students);
		return "student/student-list";
	}

	@RolesAllowed("ADMIN")
	@PostMapping("/student/delete/{studentId}")
	public String deleteStudent(@PathVariable int studentId, RedirectAttributes redirectAttributes,
			HttpServletRequest request) {
		try {
			studentService.deleteStudentById(studentId);
			redirectAttributes.addFlashAttribute("successMessage", "Student was deleted");
		} catch (NoSuchElementException ex) {
			redirectAttributes.addFlashAttribute("errorMessage", ex.getLocalizedMessage());
		}
		String referrer = request.getHeader("referer");

		if (referrer == null || referrer.isEmpty()) {
			return "redirect:/student/student-list";
		}
		return "redirect:" + referrer;
	}

	@RolesAllowed("ADMIN")
	@GetMapping("/student/student-card/{studentId}")
	public String openStudentCard(@PathVariable int studentId, Model model) {
		Optional<Student> optionalStudent = studentService.findStudentById(studentId);
		List<Course> courses = courseService.getAllCourses();
		List<Group> groups = groupService.getAllGroups();

		if (optionalStudent.isPresent()) {
			Student student = optionalStudent.get();
			model.addAttribute("student", student);
			model.addAttribute("courses", courses);
			model.addAttribute("groups", groups);
			return "student/student-card";
		} else {
			return "redirect:/student/student-list";
		}
	}

	@RolesAllowed("ADMIN")
	@PostMapping("/student/remove-course/{studentId}/{courseName}")
	public String removeStudentFromCourse(@PathVariable int studentId, @PathVariable String courseName,
			RedirectAttributes redirectAttributes) {
		try {
			studentService.removeStudentFromCourse(studentId, courseName);
			redirectAttributes.addFlashAttribute("successMessage", "Student unsubscribed from Course");
		} catch (IllegalStateException | NoSuchElementException ex) {
			redirectAttributes.addFlashAttribute("errorMessage", ex.getLocalizedMessage());
		}
		return "redirect:/student/student-card/{studentId}";
	}

	@RolesAllowed("ADMIN")
	@PostMapping("/student/assign-course")
	public String addStudentToTheCourse(@RequestParam int studentId, @RequestParam String courseName,
			RedirectAttributes redirectAttributes) {
		try {
			studentService.addStudentToTheCourse(studentId, courseName);
			redirectAttributes.addFlashAttribute("successMessage", "Student subscribed to the Course");
		} catch (IllegalStateException | NoSuchElementException ex) {
			redirectAttributes.addFlashAttribute("errorMessage", ex.getLocalizedMessage());
		}
		UriComponentsBuilder builder = UriComponentsBuilder.fromPath("/student/student-card/{studentId}");
		return "redirect:" + builder.buildAndExpand(studentId).toUriString();
	}

	@GetMapping("/student/search-result")
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
		return "student/student-list";
	}

	@RolesAllowed("ADMIN")
	@GetMapping("/student/create-student")
	public String showCreateStudentForm() {
		return "student/create-student";
	}

	@RolesAllowed("ADMIN")
	@PostMapping("/student/create-student")
	public String createStudent(@ModelAttribute("student") @Validated Student student, BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {
		if (bindingValidator.validate(bindingResult, redirectAttributes)) {
			try {
				int createdStudent = studentService.createStudent(student);

				if (createdStudent != student.getId()) {
					redirectAttributes.addFlashAttribute("errorMessage", "Failed to create the student");
				} else {
					redirectAttributes.addFlashAttribute("successMessage", "Student created successfully");
				}
			} catch (NoSuchElementException | IllegalStateException ex) {
				redirectAttributes.addFlashAttribute("errorMessage", ex.getLocalizedMessage());
			}
			return "redirect:/student/create-student";
		} else {
			return "redirect:/student/create-student";
		}
	}

	@RolesAllowed("ADMIN")
	@PostMapping("/student/edit-student/{studentId}")
	public String updateStudent(@PathVariable("studentId") int studentId,
			@ModelAttribute("student") @Validated Student updatedStudent, BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {
		if (bindingValidator.validate(bindingResult, redirectAttributes)) {
			try {
				Student resultStudent = studentService.updateStudentById(studentId, updatedStudent);

				if (resultStudent != null) {
					redirectAttributes.addFlashAttribute("successMessage", "Student updated successfully");
				} else {
					redirectAttributes.addFlashAttribute("errorMessage", "Failed to update Student");
				}
			} catch (NoSuchElementException | IllegalStateException ex) {
				redirectAttributes.addFlashAttribute("errorMessage", ex.getLocalizedMessage());
			}
		} else {
			return "redirect:/student/student-card/" + studentId;
		}
		return "redirect:/student/student-card/" + studentId;
	}

}
