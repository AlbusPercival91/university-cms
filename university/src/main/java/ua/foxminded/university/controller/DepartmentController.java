package ua.foxminded.university.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
import ua.foxminded.university.dao.entities.Department;
import ua.foxminded.university.dao.entities.Faculty;
import ua.foxminded.university.dao.entities.User;
import ua.foxminded.university.dao.service.AlertService;
import ua.foxminded.university.dao.service.DepartmentService;
import ua.foxminded.university.dao.service.FacultyService;
import ua.foxminded.university.security.UserDetailsServiceImpl;
import ua.foxminded.university.validation.ControllerBindingValidator;
import ua.foxminded.university.validation.IdCollector;
import ua.foxminded.university.validation.Message;

@Controller
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private FacultyService facultyService;

    @Autowired
    private AlertService alertService;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private ControllerBindingValidator bindingValidator;

    @Autowired
    private IdCollector idCollector;

    @PostMapping("/department/send-alert/{departmentId}")
    public String sendDepartmentAlert(@PathVariable int departmentId, @RequestParam String alertMessage,
            RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userDetailsService.getUserByUsername(email);
        String sender = user.getFirstName() + " " + user.getLastName();

        try {
            alertService.createDepartmentAlert(LocalDateTime.now(), sender, departmentId, alertMessage);

            if (alertMessage != null) {
                redirectAttributes.addFlashAttribute(Message.SUCCESS, Message.ALERT_SUCCESS);
            }
        } catch (NoSuchElementException ex) {
            redirectAttributes.addFlashAttribute(Message.ERROR, ex.getLocalizedMessage());
        }
        return "redirect:/department/department-card/" + departmentId;
    }

    @GetMapping("/department/department-list")
    public String getAllDepartmentList(Model model) {
        List<Department> departments = departmentService.getAllDepartments();

        model.addAttribute("departments", departments);
        return "department/department-list";
    }

    @RolesAllowed("ADMIN")
    @PostMapping("/department/delete/{departmentId}")
    public String deleteDepartment(@PathVariable int departmentId, RedirectAttributes redirectAttributes,
            HttpServletRequest request) {
        try {
            departmentService.deleteDepartmentById(departmentId);
            redirectAttributes.addFlashAttribute(Message.SUCCESS, Message.DELETE_SUCCESS);
        } catch (NoSuchElementException ex) {
            redirectAttributes.addFlashAttribute(Message.ERROR, ex.getLocalizedMessage());
        }
        String referrer = request.getHeader("referer");

        if (referrer == null || referrer.isEmpty()) {
            return "redirect:/department/department-list";
        }
        return "redirect:" + referrer;
    }

    @RolesAllowed("ADMIN")
    @GetMapping("/department/create-department")
    public String showCreateDepartmentForm(Model model) {
        List<Faculty> faculties = facultyService.getAllFaculties();

        model.addAttribute("faculties", faculties);
        return "department/create-department";
    }

    @RolesAllowed("ADMIN")
    @PostMapping("/department/create-department")
    public String createDepartment(@ModelAttribute("department") @Validated Department department,
            BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingValidator.validate(bindingResult, redirectAttributes)) {
            try {
                int createdDepartment = departmentService.createDepartment(department);

                if (createdDepartment != department.getId()) {
                    redirectAttributes.addFlashAttribute(Message.ERROR, Message.FAILURE);
                } else {
                    redirectAttributes.addFlashAttribute(Message.SUCCESS, Message.CREATE_SUCCESS);
                }
            } catch (IllegalStateException ex) {
                redirectAttributes.addFlashAttribute(Message.ERROR, ex.getLocalizedMessage());
            }
        }
        return "redirect:/department/create-department";
    }

    @GetMapping("/department/search-result")
    public String searchDepartment(@RequestParam("searchType") String searchType,
            @RequestParam(required = false) String departmentId, @RequestParam(required = false) String name,
            @RequestParam(required = false) String facultyName, Model model) {
        List<Department> departmentList = new ArrayList<>();

        if ("department".equals(searchType)) {
            Optional<Department> optionalDepartment = departmentService
                    .findDepartmentById(idCollector.collect(departmentId));
            departmentList = optionalDepartment.map(Collections::singletonList).orElse(Collections.emptyList());
        } else if ("name".equals(searchType)) {
            departmentList = departmentService.findDepartmentByName(name);
        } else if ("faculty".equals(searchType)) {
            departmentList = departmentService.findAllByFacultyName(facultyName);
        }
        model.addAttribute("departments", departmentList);
        return "department/department-list";
    }

    @RolesAllowed({ "ADMIN", "STAFF" })
    @GetMapping("/department/department-card/{departmentId}")
    public String openDepartmentCard(@PathVariable int departmentId, Model model) {
        Optional<Department> optionalDepartment = departmentService.findDepartmentById(departmentId);
        List<Faculty> faculties = facultyService.getAllFaculties();

        if (optionalDepartment.isPresent()) {
            Department department = optionalDepartment.get();
            model.addAttribute("department", department);
            model.addAttribute("faculties", faculties);
            return "department/department-card";
        } else {
            return "redirect:/department/department-list";
        }
    }

    @RolesAllowed("ADMIN")
    @PostMapping("/department/edit-department/{departmentId}")
    public String updateDepartment(@PathVariable("departmentId") int departmentId,
            @ModelAttribute("department") @Validated Department updatedDepartment, BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingValidator.validate(bindingResult, redirectAttributes)) {
            try {
                Department resultDepartment = departmentService.updateDepartmentById(departmentId, updatedDepartment);

                if (resultDepartment != null) {
                    redirectAttributes.addFlashAttribute(Message.SUCCESS, Message.UPDATE_SUCCESS);
                } else {
                    redirectAttributes.addFlashAttribute(Message.ERROR, Message.FAILURE);
                }
            } catch (NoSuchElementException | IllegalStateException ex) {
                redirectAttributes.addFlashAttribute(Message.ERROR, ex.getLocalizedMessage());
            }
        }
        return "redirect:/department/department-card/" + departmentId;
    }
}
