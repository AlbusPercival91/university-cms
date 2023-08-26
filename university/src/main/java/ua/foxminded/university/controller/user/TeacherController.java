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
import ua.foxminded.university.dao.entities.Department;
import ua.foxminded.university.dao.entities.Teacher;
import ua.foxminded.university.dao.service.CourseService;
import ua.foxminded.university.dao.service.DepartmentService;
import ua.foxminded.university.dao.service.TeacherService;
import ua.foxminded.university.validation.ControllerBindingValidator;

@Controller
public class TeacherController {

	@Autowired
	private TeacherService teacherService;

	@Autowired
	private DepartmentService departmentService;

	@Autowired
	private CourseService courseService;

	@Autowired
	private ControllerBindingValidator bindingValidator;

	@GetMapping("/user/teacher/main")
	public String teacherMainPage(Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.isAuthenticated()) {
			String email = authentication.getName();
			Optional<Teacher> teacher = teacherService.findTeacherByEmail(email);

			if (teacher.isPresent()) {
				List<Course> courses = courseService.getAllCourses();
				List<Department> departments = departmentService.getAllDepartments();
				model.addAttribute("teacher", teacher.get());
				model.addAttribute("courses", courses);
				model.addAttribute("departments", departments);
				return "user/teacher/main";
			}
		}
		return "redirect:/login";
	}

	@PostMapping("/user/teacher/update-personal/{teacherId}")
	public String updatePersonalData(@PathVariable("teacherId") int teacherId,
			@ModelAttribute("teacher") @Validated Teacher updatedTeacher, BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {
		if (bindingValidator.validate(bindingResult, redirectAttributes)) {
			try {
				Teacher resultTeacher = teacherService.updateTeacherById(teacherId, updatedTeacher);

				if (resultTeacher != null) {
					redirectAttributes.addFlashAttribute("successMessage", "Data updated successfully");
				} else {
					redirectAttributes.addFlashAttribute("errorMessage", "Failed to update Data");
				}
			} catch (NoSuchElementException | IllegalStateException ex) {
				redirectAttributes.addFlashAttribute("errorMessage", ex.getLocalizedMessage());
			}
		} else {
			return "redirect:/user/teacher/main";
		}
		return "redirect:/user/teacher/main";
	}

	@PostMapping("/user/teacher/update-password")
	public String updatePassword(@RequestParam int teacherId, @RequestParam String oldPassword,
			@RequestParam String newPassword, RedirectAttributes redirectAttributes) {
		try {
			Teacher resultTeacher = teacherService.changeTeacherPasswordById(teacherId, oldPassword, newPassword);
			if (resultTeacher != null) {
				redirectAttributes.addFlashAttribute("successMessage", "Password changed successfully");
			} else {
				redirectAttributes.addFlashAttribute("errorMessage", "Failed to change Password");
			}
		} catch (NoSuchElementException | IllegalStateException ex) {
			redirectAttributes.addFlashAttribute("errorMessage", ex.getLocalizedMessage());
		}
		return "redirect:/user/teacher/main";
	}

	@GetMapping("/user/teacher-list")
	public String getAllTeachersList(Model model) {
		List<Teacher> teachers = teacherService.getAllTeachers();

		for (Teacher teacher : teachers) {
			teacher.getAssignedCourses();
		}
		model.addAttribute("teachers", teachers);
		return "user/teacher-list";
	}

	@GetMapping("/user/teacher/search-result")
	public String searchTeachers(@RequestParam("searchType") String searchType,
			@RequestParam(required = false) String courseName, @RequestParam(required = false) String facultyName,
			@RequestParam(required = false) Integer departmentId, @RequestParam(required = false) Integer facultyId,
			@RequestParam(required = false) Integer teacherId, @RequestParam(required = false) String firstName,
			@RequestParam(required = false) String lastName, Model model) {
		List<Teacher> teachers;

		if ("course".equals(searchType)) {
			teachers = teacherService.findTeachersRelatedToCourse(courseName);
		} else if ("faculty".equals(searchType)) {
			teachers = teacherService.findAllByFacultyName(facultyName);
		} else if ("department".equals(searchType)) {
			teachers = teacherService.findAllByDepartmentIdAndDepartmentFacultyId(departmentId, facultyId);
		} else if ("teacher".equals(searchType)) {
			Optional<Teacher> optionalTeacher = teacherService.findTeacherById(teacherId);
			teachers = optionalTeacher.map(Collections::singletonList).orElse(Collections.emptyList());
		} else if ("firstNameAndLastName".equals(searchType)) {
			teachers = teacherService.findTeacherByName(firstName, lastName);
		} else {
			return "error";
		}
		teachers.forEach(Teacher::getAssignedCourses);
		model.addAttribute("teachers", teachers);
		return "user/teacher-list";
	}

}
