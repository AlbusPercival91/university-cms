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
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ua.foxminded.university.dao.entities.Course;
import ua.foxminded.university.dao.service.CourseService;

@Controller
public class AdminCourseController {

	@Autowired
	private CourseService courseService;

	@GetMapping("/admin/course/edit-course-list")
	public String getAllCourseListAsAdmin(Model model) {
		List<Course> courses = courseService.getAllCourses();

		model.addAttribute("courses", courses);
		return "admin/course/edit-course-list";
	}

	@PostMapping("admin/course/delete/{courseId}")
	public String deleteCourse(@PathVariable int courseId, RedirectAttributes redirectAttributes,
			HttpServletRequest request) {
		try {
			courseService.deleteCourseById(courseId);
			redirectAttributes.addFlashAttribute("successMessage", "Course was deleted!");
		} catch (NoSuchElementException ex) {
			redirectAttributes.addFlashAttribute("errorMessage", ex.getLocalizedMessage());
		}
		String referrer = request.getHeader("referer");

		if (referrer == null || referrer.isEmpty()) {
			return "redirect:/admin/course/edit-course-list";
		}
		return "redirect:" + referrer;
	}

	@GetMapping("/admin/course/create-course")
	public String showCreateCourseForm() {
		return "admin/course/create-course";
	}

	@PostMapping("/admin/course/create-course")
	public String createCourse(@ModelAttribute("course") @Validated Course course, BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			for (FieldError error : bindingResult.getFieldErrors()) {
				redirectAttributes.addFlashAttribute(error.getField() + "Error", error.getDefaultMessage());
			}
			return "redirect:/admin/course/create-course";
		}

		try {
			int createdCourse = courseService.createCourse(course);

			if (createdCourse != course.getId()) {
				redirectAttributes.addFlashAttribute("errorMessage", "Failed to create Course");
			} else {
				redirectAttributes.addFlashAttribute("successMessage", "Course created successfully");
			}
		} catch (IllegalStateException ex) {
			redirectAttributes.addFlashAttribute("errorMessage", ex.getLocalizedMessage());
		}
		return "redirect:/admin/course/create-course";
	}

	@GetMapping("/admin/course/search-result")
	public String searchCourseAsAdmin(@RequestParam("searchType") String searchType,
			@RequestParam(required = false) Integer courseId, @RequestParam(required = false) String courseName,
			@RequestParam(required = false) Integer teacherId, @RequestParam(required = false) Integer studentId,
			Model model) {
		List<Course> courseList;

		if ("course".equals(searchType)) {
			Optional<Course> optionalCourse = courseService.findCourseById(courseId);
			courseList = optionalCourse.map(Collections::singletonList).orElse(Collections.emptyList());
		} else if ("courseName".equals(searchType)) {
			Optional<Course> optionalCourse = courseService.findByCourseName(courseName);
			courseList = optionalCourse.map(Collections::singletonList).orElse(Collections.emptyList());
		} else if ("teacher".equals(searchType)) {
			courseList = courseService.findCoursesRelatedToTeacher(teacherId);
		} else if ("student".equals(searchType)) {
			courseList = courseService.findCoursesRelatedToStudent(studentId);
		} else {
			return "error";
		}
		model.addAttribute("courses", courseList);
		return "admin/course/edit-course-list";
	}

	@GetMapping("/admin/course/course-card/{courseId}")
	public String openCourseCard(@PathVariable int courseId, Model model) {
		Optional<Course> optionalCourse = courseService.findCourseById(courseId);

		if (optionalCourse.isPresent()) {
			Course course = optionalCourse.get();
			model.addAttribute("course", course);
			return "admin/course/course-card";
		} else {
			return "redirect:/admin/course/edit-course-list";
		}
	}
}
