package ua.foxminded.university.controller.admin;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
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
import ua.foxminded.university.dao.entities.Department;
import ua.foxminded.university.dao.entities.Teacher;
import ua.foxminded.university.dao.service.CourseService;
import ua.foxminded.university.dao.service.DepartmentService;
import ua.foxminded.university.dao.service.TeacherService;
import ua.foxminded.university.validation.ControllerBindingValidator;

@Controller
public class AdminTeacherController {

	@Autowired
	private TeacherService teacherService;

	@Autowired
	private DepartmentService departmentService;

	@Autowired
	private CourseService courseService;

	@Autowired
	private ControllerBindingValidator bindingValidator;

	@GetMapping("/admin/teacher/create-teacher")
	public String showCreateTeacherForm(Model model) {
		List<Department> departments = departmentService.getAllDepartments();
		List<Course> courses = courseService.getAllCourses();
		model.addAttribute("departments", departments);
		model.addAttribute("courses", courses);
		return "admin/teacher/create-teacher";
	}

	@PostMapping("/admin/teacher/create-teacher")
	public String createTeacher(@ModelAttribute("teacher") @Validated Teacher teacher, @Validated Course course,
			BindingResult bindingResult, RedirectAttributes redirectAttributes) {
		if (bindingValidator.validate(bindingResult, redirectAttributes)) {
			try {
				int createdTeacher = teacherService.createAndAssignTeacherToCourse(teacher, course);

				if (createdTeacher != teacher.getId()) {
					redirectAttributes.addFlashAttribute("errorMessage", "Failed to create the teacher");
				} else {
					redirectAttributes.addFlashAttribute("successMessage", "Teacher created successfully");
				}
			} catch (IllegalStateException ex) {
				redirectAttributes.addFlashAttribute("errorMessage", ex.getLocalizedMessage());
			}
			return "redirect:/admin/teacher/create-teacher";
		} else {
			return "redirect:/admin/teacher/create-teacher";
		}
	}

	@GetMapping("/admin/teacher/teacher-card/{teacherId}")
	public String openTeacherCard(@PathVariable int teacherId, Model model) {
		Optional<Teacher> optionalTeacher = teacherService.findTeacherById(teacherId);
		List<Course> courses = courseService.getAllCourses();
		List<Department> departments = departmentService.getAllDepartments();

		if (optionalTeacher.isPresent()) {
			Teacher teacher = optionalTeacher.get();
			model.addAttribute("teacher", teacher);
			model.addAttribute("courses", courses);
			model.addAttribute("departments", departments);
			return "admin/teacher/teacher-card";
		} else {
			return "redirect:/admin/teacher/edit-teacher-list";
		}
	}

	@GetMapping("/admin/teacher/edit-teacher-list")
	public String getAllTeachersListAsAdmin(Model model) {
		List<Teacher> teachers = teacherService.getAllTeachers();

		for (Teacher teacher : teachers) {
			teacher.getAssignedCourses();
		}
		model.addAttribute("teachers", teachers);
		return "admin/teacher/edit-teacher-list";
	}

	@GetMapping("/admin/teacher/search-result")
	public String searchTeachersAsAdmin(@RequestParam("searchType") String searchType,
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
		return "admin/teacher/edit-teacher-list";
	}

	@PostMapping("/admin/teacher/delete/{teacherId}")
	public String deleteTeacher(@PathVariable int teacherId, RedirectAttributes redirectAttributes,
			HttpServletRequest request) {
		try {
			teacherService.deleteTeacherById(teacherId);
			redirectAttributes.addFlashAttribute("successMessage", "Teacher was deleted!");
		} catch (NoSuchElementException ex) {
			redirectAttributes.addFlashAttribute("errorMessage", ex.getLocalizedMessage());
		}
		String referrer = request.getHeader("referer");

		if (referrer == null || referrer.isEmpty()) {
			return "redirect:/admin/teacher/edit-teacher-list";
		}
		return "redirect:" + referrer;
	}

	@PostMapping("/admin/teacher/remove-course/{teacherId}/{courseName}")
	public String removeTeacherFromCourse(@PathVariable int teacherId, @PathVariable String courseName,
			RedirectAttributes redirectAttributes) {
		try {
			teacherService.removeTeacherFromCourse(teacherId, courseName);
			redirectAttributes.addFlashAttribute("successMessage", "Teacher unsubscribed from Course!");
		} catch (IllegalStateException | NoSuchElementException ex) {
			redirectAttributes.addFlashAttribute("errorMessage", ex.getLocalizedMessage());
		}
		return "redirect:/admin/teacher/teacher-card/{teacherId}";
	}

	@PostMapping("/admin/teacher/assign-course")
	public String addTeacherToTheCourse(@RequestParam int teacherId, @RequestParam String courseName,
			RedirectAttributes redirectAttributes) {
		try {
			teacherService.addTeacherToTheCourse(teacherId, courseName);
			redirectAttributes.addFlashAttribute("successMessage", "Teacher subscribed to Course!");
		} catch (IllegalStateException | NoSuchElementException ex) {
			redirectAttributes.addFlashAttribute("errorMessage", ex.getLocalizedMessage());
		}
		UriComponentsBuilder builder = UriComponentsBuilder.fromPath("/admin/teacher/teacher-card/{teacherId}");
		return "redirect:" + builder.buildAndExpand(teacherId).toUriString();
	}

	@PostMapping("/admin/teacher/edit-teacher/{teacherId}")
	public String updateTeacher(@PathVariable("teacherId") int teacherId,
			@ModelAttribute("teacher") @Validated Teacher updatedTeacher, BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {
		if (bindingValidator.validate(bindingResult, redirectAttributes)) {
			try {
				Teacher resultTeacher = teacherService.updateTeacherById(teacherId, updatedTeacher);

				if (resultTeacher != null) {
					redirectAttributes.addFlashAttribute("successMessage", "Teacher updated successfully");
				} else {
					redirectAttributes.addFlashAttribute("errorMessage", "Failed to update Teacher");
				}
			} catch (NoSuchElementException ex) {
				redirectAttributes.addFlashAttribute("errorMessage", "Teacher not found");
			}
		} else {
			return "redirect:/admin/teacher/teacher-card/" + teacherId;
		}
		return "redirect:/admin/teacher/teacher-card/" + teacherId;
	}

}