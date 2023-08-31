package ua.foxminded.university.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import javax.annotation.security.RolesAllowed;
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
import ua.foxminded.university.dao.entities.Course;
import ua.foxminded.university.dao.service.CourseService;
import ua.foxminded.university.validation.ControllerBindingValidator;
import ua.foxminded.university.validation.Message;

@Controller
public class CourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private ControllerBindingValidator bindingValidator;

    @GetMapping("/course/course-list")
    public String getAllCourseList(Model model) {
        List<Course> courses = courseService.getAllCourses();

        model.addAttribute("courses", courses);
        return "course/course-list";
    }

    @RolesAllowed("ADMIN")
    @PostMapping("/course/delete/{courseId}")
    public String deleteCourse(@PathVariable int courseId, RedirectAttributes redirectAttributes,
            HttpServletRequest request) {
        try {
            courseService.deleteCourseById(courseId);
            redirectAttributes.addFlashAttribute(Message.SUCCESS, Message.DELETE_SUCCESS);
        } catch (NoSuchElementException ex) {
            redirectAttributes.addFlashAttribute(Message.ERROR, ex.getLocalizedMessage());
        }
        String referrer = request.getHeader("referer");

        if (referrer == null || referrer.isEmpty()) {
            return "redirect:/course/course-list";
        }
        return "redirect:" + referrer;
    }

    @RolesAllowed("ADMIN")
    @GetMapping("/course/create-course")
    public String showCreateCourseForm() {
        return "course/create-course";
    }

    @RolesAllowed("ADMIN")
    @PostMapping("/course/create-course")
    public String createCourse(@ModelAttribute("course") @Validated Course course, BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingValidator.validate(bindingResult, redirectAttributes)) {
            try {
                int createdCourse = courseService.createCourse(course);

                if (createdCourse != course.getId()) {
                    redirectAttributes.addFlashAttribute(Message.ERROR, Message.FAILURE);
                } else {
                    redirectAttributes.addFlashAttribute(Message.SUCCESS, Message.CREATE_SUCCESS);
                }
            } catch (IllegalStateException ex) {
                redirectAttributes.addFlashAttribute(Message.ERROR, ex.getLocalizedMessage());
            }
        }
        return "redirect:/course/create-course";
    }

    @GetMapping("/course/search-result")
    public String searchCourse(@RequestParam("searchType") String searchType,
            @RequestParam(required = false) Integer courseId, @RequestParam(required = false) String courseName,
            @RequestParam(required = false) Integer teacherId, @RequestParam(required = false) Integer studentId,
            Model model) {
        List<Course> courseList = new ArrayList<>();

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
        }
        model.addAttribute("courses", courseList);
        return "course/course-list";
    }

    @RolesAllowed("ADMIN")
    @GetMapping("/course/course-card/{courseId}")
    public String openCourseCard(@PathVariable int courseId, Model model) {
        Optional<Course> optionalCourse = courseService.findCourseById(courseId);

        if (optionalCourse.isPresent()) {
            Course course = optionalCourse.get();
            model.addAttribute("course", course);
            return "course/course-card";
        } else {
            return "redirect:/course/course-list";
        }
    }

    @RolesAllowed("ADMIN")
    @PostMapping("/course/edit-course/{courseId}")
    public String updateCourse(@PathVariable("courseId") int courseId,
            @ModelAttribute("course") @Validated Course updatedCourse, BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingValidator.validate(bindingResult, redirectAttributes)) {
            try {
                Course resultCourse = courseService.updateCourseById(courseId, updatedCourse);

                if (resultCourse != null) {
                    redirectAttributes.addFlashAttribute(Message.SUCCESS, Message.UPDATE_SUCCESS);
                } else {
                    redirectAttributes.addFlashAttribute(Message.ERROR, Message.FAILURE);
                }
            } catch (NoSuchElementException ex) {
                redirectAttributes.addFlashAttribute(Message.ERROR, Message.NOT_FOUND);
            }
        }
        return "redirect:/course/course-card/" + courseId;
    }
}
