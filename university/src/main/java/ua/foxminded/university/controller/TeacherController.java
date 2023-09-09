package ua.foxminded.university.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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
import ua.foxminded.university.dao.entities.Alert;
import ua.foxminded.university.dao.entities.Course;
import ua.foxminded.university.dao.entities.Department;
import ua.foxminded.university.dao.entities.Teacher;
import ua.foxminded.university.dao.service.AlertService;
import ua.foxminded.university.dao.service.CourseService;
import ua.foxminded.university.dao.service.DepartmentService;
import ua.foxminded.university.dao.service.TeacherService;
import ua.foxminded.university.validation.ControllerBindingValidator;
import ua.foxminded.university.validation.Message;

@Controller
public class TeacherController {

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private AlertService alertService;

    @Autowired
    private ControllerBindingValidator bindingValidator;

    @GetMapping("/teacher/main")
    public String teacherDashboard(Model model) {
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
                return "teacher/main";
            }
        }
        return "redirect:/login";
    }

    @PostMapping("/teacher/update-personal/{teacherId}")
    public String updatePersonalData(@PathVariable("teacherId") int teacherId,
            @ModelAttribute("teacher") @Validated Teacher updatedTeacher, BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingValidator.validate(bindingResult, redirectAttributes)) {
            try {
                Teacher resultTeacher = teacherService.updateTeacherById(teacherId, updatedTeacher);

                if (resultTeacher != null) {
                    redirectAttributes.addFlashAttribute(Message.SUCCESS, Message.UPDATE_SUCCESS);
                } else {
                    redirectAttributes.addFlashAttribute(Message.ERROR, Message.FAILURE);
                }
            } catch (NoSuchElementException | IllegalStateException ex) {
                redirectAttributes.addFlashAttribute(Message.ERROR, ex.getLocalizedMessage());
            }
        }
        return "redirect:/teacher/main";
    }

    @PostMapping("/teacher/update-password")
    public String updatePassword(@RequestParam int teacherId, @RequestParam String oldPassword,
            @RequestParam String newPassword, RedirectAttributes redirectAttributes) {
        try {
            Teacher resultTeacher = teacherService.changeTeacherPasswordById(teacherId, oldPassword, newPassword);
            if (resultTeacher != null) {
                redirectAttributes.addFlashAttribute(Message.SUCCESS, Message.UPDATE_SUCCESS);
            } else {
                redirectAttributes.addFlashAttribute(Message.ERROR, Message.FAILURE);
            }
        } catch (NoSuchElementException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute(Message.ERROR, ex.getLocalizedMessage());
        }
        return "redirect:/teacher/main";
    }

    @GetMapping("/teacher/alert")
    public String openTeacherAlerts(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Optional<Teacher> teacher = teacherService.findTeacherByEmail(email);

        if (teacher.isPresent()) {
            List<Alert> alerts = alertService.getAllTeacherAlerts(teacher.get());
            model.addAttribute("teacher", teacher.get());
            model.addAttribute("alerts", alerts);
        }
        return "alert";
    }

    @PostMapping("/teacher/send-alert/{teacherId}")
    public String sendTeacherAlert(@PathVariable int teacherId, @RequestParam String alertMessage,
            RedirectAttributes redirectAttributes) {
        try {
            alertService.createTeacherAlert(LocalDateTime.now(), teacherId, alertMessage);

            if (alertMessage != null) {
                redirectAttributes.addFlashAttribute(Message.SUCCESS, Message.ALERT_SUCCESS);
            }
        } catch (NoSuchElementException ex) {
            redirectAttributes.addFlashAttribute(Message.ERROR, ex.getLocalizedMessage());
        }
        return "redirect:/teacher/teacher-card/" + teacherId;
    }

    @PostMapping("/teacher/remove-alert/{alertId}")
    public String deleteTeacherAlert(@PathVariable int alertId, RedirectAttributes redirectAttributes,
            HttpServletRequest request) {
        try {
            alertService.deleteAlertById(alertId);
            redirectAttributes.addFlashAttribute(Message.SUCCESS, Message.DELETE_SUCCESS);
        } catch (NoSuchElementException ex) {
            redirectAttributes.addFlashAttribute(Message.ERROR, ex.getLocalizedMessage());
        }
        String referrer = request.getHeader("referer");

        if (referrer == null || referrer.isEmpty()) {
            return "redirect:/teacher/alert";
        }
        return "redirect:" + referrer;
    }

    @PostMapping("/teacher/mark-alert-as-read/{alertId}")
    public String toggleTeacherAlert(@PathVariable int alertId, RedirectAttributes redirectAttributes,
            HttpServletRequest request) {
        try {
            alertService.toggleRead(alertId);
        } catch (NoSuchElementException ex) {
            redirectAttributes.addFlashAttribute(Message.ERROR, ex.getLocalizedMessage());
        }
        String referrer = request.getHeader("referer");

        if (referrer == null || referrer.isEmpty()) {
            return "redirect:/teacher/alert";
        }
        return "redirect:" + referrer;
    }

    @GetMapping("/teacher/selected-alert/{teacherId}")
    public String getSelectedDateTeacherAlerts(@PathVariable("teacherId") int teacherId,
            @RequestParam("dateFrom") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateFrom,
            @RequestParam("dateTo") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateTo, Model model) {
        Optional<Teacher> teacher = teacherService.findTeacherById(teacherId);

        LocalDateTime from = dateFrom.atStartOfDay();
        LocalDateTime to = dateTo.atTime(LocalTime.MAX);

        if (teacher.isPresent()) {
            List<Alert> alerts = alertService.findByTeacherAndDateBetween(teacherId, from, to);
            model.addAttribute("teacher", teacher.get());
            model.addAttribute("alerts", alerts);
        }
        return "alert";
    }

    @RolesAllowed("ADMIN")
    @GetMapping("/teacher/create-teacher")
    public String showCreateTeacherForm(Model model) {
        List<Department> departments = departmentService.getAllDepartments();
        List<Course> courses = courseService.getAllCourses();
        model.addAttribute("departments", departments);
        model.addAttribute("courses", courses);
        return "teacher/create-teacher";
    }

    @RolesAllowed("ADMIN")
    @PostMapping("/teacher/create-teacher")
    public String createTeacher(@ModelAttribute("teacher") @Validated Teacher teacher, @Validated Course course,
            BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingValidator.validate(bindingResult, redirectAttributes)) {
            try {
                int createdTeacher = teacherService.createAndAssignTeacherToCourse(teacher, course);

                if (createdTeacher != teacher.getId()) {
                    redirectAttributes.addFlashAttribute(Message.ERROR, Message.FAILURE);
                } else {
                    redirectAttributes.addFlashAttribute(Message.SUCCESS, Message.CREATE_SUCCESS);
                }
            } catch (IllegalStateException ex) {
                redirectAttributes.addFlashAttribute(Message.ERROR, ex.getLocalizedMessage());
            }
        }
        return "redirect:/teacher/create-teacher";
    }

    @RolesAllowed({ "ADMIN", "TEACHER", "STAFF" })
    @GetMapping("/teacher/teacher-card/{teacherId}")
    public String openTeacherCard(@PathVariable int teacherId, Model model) {
        Optional<Teacher> optionalTeacher = teacherService.findTeacherById(teacherId);
        List<Course> courses = courseService.getAllCourses();
        List<Department> departments = departmentService.getAllDepartments();

        if (optionalTeacher.isPresent()) {
            Teacher teacher = optionalTeacher.get();
            model.addAttribute("teacher", teacher);
            model.addAttribute("courses", courses);
            model.addAttribute("departments", departments);
            return "teacher/teacher-card";
        } else {
            return "redirect:/teacher/teacher-list";
        }
    }

    @GetMapping("/teacher/teacher-list")
    public String getAllTeachersList(Model model) {
        List<Teacher> teachers = teacherService.getAllTeachers();

        for (Teacher teacher : teachers) {
            teacher.getAssignedCourses();
        }
        model.addAttribute("teachers", teachers);
        return "teacher/teacher-list";
    }

    @GetMapping("/teacher/search-result")
    public String searchTeachers(@RequestParam("searchType") String searchType,
            @RequestParam(required = false) String courseName, @RequestParam(required = false) String facultyName,
            @RequestParam(required = false) Integer departmentId, @RequestParam(required = false) Integer facultyId,
            @RequestParam(required = false) Integer teacherId, @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName, Model model) {
        List<Teacher> teachers = new ArrayList<>();

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
        }
        teachers.forEach(Teacher::getAssignedCourses);
        model.addAttribute("teachers", teachers);
        return "teacher/teacher-list";
    }

    @RolesAllowed("ADMIN")
    @PostMapping("/teacher/delete/{teacherId}")
    public String deleteTeacher(@PathVariable int teacherId, RedirectAttributes redirectAttributes,
            HttpServletRequest request) {
        try {
            teacherService.deleteTeacherById(teacherId);
            redirectAttributes.addFlashAttribute(Message.SUCCESS, Message.DELETE_SUCCESS);
        } catch (NoSuchElementException ex) {
            redirectAttributes.addFlashAttribute(Message.ERROR, ex.getLocalizedMessage());
        }
        String referrer = request.getHeader("referer");

        if (referrer == null || referrer.isEmpty()) {
            return "redirect:/teacher/teacher-list";
        }
        return "redirect:" + referrer;
    }

    @RolesAllowed({ "ADMIN", "STAFF" })
    @PostMapping("/teacher/remove-course/{teacherId}/{courseName}")
    public String removeTeacherFromCourse(@PathVariable int teacherId, @PathVariable String courseName,
            RedirectAttributes redirectAttributes) {
        try {
            teacherService.removeTeacherFromCourse(teacherId, courseName);
            redirectAttributes.addFlashAttribute(Message.SUCCESS, Message.REASSIGNED);
        } catch (IllegalStateException | NoSuchElementException ex) {
            redirectAttributes.addFlashAttribute(Message.ERROR, ex.getLocalizedMessage());
        }
        return "redirect:/teacher/teacher-card/{teacherId}";
    }

    @RolesAllowed({ "ADMIN", "STAFF" })
    @PostMapping("/teacher/assign-course")
    public String addTeacherToTheCourse(@RequestParam int teacherId, @RequestParam String courseName,
            RedirectAttributes redirectAttributes) {
        try {
            teacherService.addTeacherToTheCourse(teacherId, courseName);
            redirectAttributes.addFlashAttribute(Message.SUCCESS, Message.ASSIGNED);
        } catch (IllegalStateException | NoSuchElementException ex) {
            redirectAttributes.addFlashAttribute(Message.ERROR, ex.getLocalizedMessage());
        }
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath("/teacher/teacher-card/{teacherId}");
        return "redirect:" + builder.buildAndExpand(teacherId).toUriString();
    }

    @RolesAllowed("ADMIN")
    @PostMapping("/teacher/edit-teacher/{teacherId}")
    public String updateTeacher(@PathVariable("teacherId") int teacherId,
            @ModelAttribute("teacher") @Validated Teacher updatedTeacher, BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingValidator.validate(bindingResult, redirectAttributes)) {
            try {
                Teacher resultTeacher = teacherService.updateTeacherById(teacherId, updatedTeacher);

                if (resultTeacher != null) {
                    redirectAttributes.addFlashAttribute(Message.SUCCESS, Message.UPDATE_SUCCESS);
                } else {
                    redirectAttributes.addFlashAttribute(Message.ERROR, Message.FAILURE);
                }
            } catch (NoSuchElementException | IllegalStateException ex) {
                redirectAttributes.addFlashAttribute(Message.ERROR, ex.getLocalizedMessage());
            }
        }
        return "redirect:/teacher/teacher-card/" + teacherId;
    }

}
