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
import ua.foxminded.university.dao.entities.Alert;
import ua.foxminded.university.dao.entities.Staff;
import ua.foxminded.university.dao.service.AlertService;
import ua.foxminded.university.dao.service.StaffService;
import ua.foxminded.university.validation.ControllerBindingValidator;
import ua.foxminded.university.validation.Message;

@Controller
public class StaffController {

    @Autowired
    private StaffService staffService;

    @Autowired
    private AlertService alertService;

    @Autowired
    private ControllerBindingValidator bindingValidator;

    @GetMapping("/staff/main")
    public String staffDashboard(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            Optional<Staff> staff = staffService.findStaffByEmail(email);
            if (staff.isPresent()) {
                model.addAttribute("staff", staff.get());
                return "staff/main";
            }
        }
        return "redirect:/login";
    }

    @PostMapping("/staff/update-personal/{staffId}")
    public String updatePersonalData(@PathVariable("staffId") int staffId,
            @ModelAttribute("staff") @Validated Staff updatedStaff, BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingValidator.validate(bindingResult, redirectAttributes)) {
            try {
                Staff resultStaff = staffService.updateStaffById(staffId, updatedStaff);

                if (resultStaff != null) {
                    redirectAttributes.addFlashAttribute(Message.SUCCESS, Message.UPDATE_SUCCESS);
                } else {
                    redirectAttributes.addFlashAttribute(Message.ERROR, Message.FAILURE);
                }
            } catch (NoSuchElementException | IllegalStateException ex) {
                redirectAttributes.addFlashAttribute(Message.ERROR, ex.getLocalizedMessage());
            }
        }
        return "redirect:/staff/main";
    }

    @PostMapping("/staff/update-password")
    public String updatePassword(@RequestParam int staffId, @RequestParam String oldPassword,
            @RequestParam String newPassword, RedirectAttributes redirectAttributes) {
        try {
            Staff resultStaff = staffService.changeStaffPasswordById(staffId, oldPassword, newPassword);
            if (resultStaff != null) {
                redirectAttributes.addFlashAttribute(Message.SUCCESS, Message.UPDATE_SUCCESS);
            } else {
                redirectAttributes.addFlashAttribute(Message.ERROR, Message.FAILURE);
            }
        } catch (NoSuchElementException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute(Message.ERROR, ex.getLocalizedMessage());
        }
        return "redirect:/staff/main";
    }

    @GetMapping("/staff/alert")
    public String openStaffAlerts(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Optional<Staff> staff = staffService.findStaffByEmail(email);

        if (staff.isPresent()) {
            List<Alert> alerts = alertService.getAllStaffAlerts(staff.get());
            model.addAttribute("staff", staff.get());
            model.addAttribute("alerts", alerts);
        }
        return "alert";
    }

    @PostMapping("/staff/send-alert/{staffId}")
    public String sendStaffAlert(@PathVariable int staffId, @RequestParam String alertMessage,
            RedirectAttributes redirectAttributes) {
        try {
            alertService.createStaffAlert(LocalDateTime.now(), staffId, alertMessage);

            if (alertMessage != null) {
                redirectAttributes.addFlashAttribute(Message.SUCCESS, Message.ALERT_SUCCESS);
            }
        } catch (NoSuchElementException ex) {
            redirectAttributes.addFlashAttribute(Message.ERROR, ex.getLocalizedMessage());
        }
        return "redirect:/staff/staff-card/" + staffId;
    }

    @RolesAllowed("ADMIN")
    @GetMapping("/staff/create-staff")
    public String showCreateStaffForm() {
        return "staff/create-staff";
    }

    @RolesAllowed("ADMIN")
    @PostMapping("/staff/create-staff")
    public String createStaff(@ModelAttribute("staff") @Validated Staff staff, BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingValidator.validate(bindingResult, redirectAttributes)) {
            try {
                int createdStaff = staffService.createStaff(staff);

                if (createdStaff != staff.getId()) {
                    redirectAttributes.addFlashAttribute(Message.ERROR, Message.FAILURE);
                } else {
                    redirectAttributes.addFlashAttribute(Message.SUCCESS, Message.CREATE_SUCCESS);
                }
            } catch (IllegalStateException ex) {
                redirectAttributes.addFlashAttribute(Message.ERROR, ex.getLocalizedMessage());
            }
        }
        return "redirect:/staff/create-staff";
    }

    @GetMapping("/staff/staff-list")
    public String getAllStaffList(Model model) {
        List<Staff> staff = staffService.getAllStaff();

        model.addAttribute("staff", staff);
        return "staff/staff-list";
    }

    @RolesAllowed("ADMIN")
    @PostMapping("/staff/delete/{staffId}")
    public String deleteStaff(@PathVariable int staffId, RedirectAttributes redirectAttributes,
            HttpServletRequest request) {
        try {
            staffService.deleteStaffById(staffId);
            redirectAttributes.addFlashAttribute(Message.SUCCESS, Message.DELETE_SUCCESS);
        } catch (NoSuchElementException ex) {
            redirectAttributes.addFlashAttribute(Message.ERROR, ex.getLocalizedMessage());
        }
        String referrer = request.getHeader("referer");

        if (referrer == null || referrer.isEmpty()) {
            return "redirect:/staff/staff-list";
        }
        return "redirect:" + referrer;
    }

    @GetMapping("/staff/search-result")
    public String searchStaff(@RequestParam("searchType") String searchType,
            @RequestParam(required = false) Integer staffId, @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName, @RequestParam(required = false) String position,
            Model model) {
        List<Staff> staffList = new ArrayList<>();

        if ("staff".equals(searchType)) {
            Optional<Staff> optionalStaff = staffService.findStaffById(staffId);
            staffList = optionalStaff.map(Collections::singletonList).orElse(Collections.emptyList());
        } else if ("firstNameAndLastName".equals(searchType)) {
            staffList = staffService.findStaffByName(firstName, lastName);
        } else if ("position".equals(searchType)) {
            staffList = staffService.findStaffByPosition(position);
        }
        model.addAttribute("staff", staffList);
        return "staff/staff-list";
    }

    @RolesAllowed({ "ADMIN", "TEACHER", "STAFF" })
    @GetMapping("/staff/staff-card/{staffId}")
    public String openStaffCard(@PathVariable int staffId, Model model) {
        Optional<Staff> optionalStaff = staffService.findStaffById(staffId);

        if (optionalStaff.isPresent()) {
            Staff staff = optionalStaff.get();
            model.addAttribute("staff", staff);
            return "staff/staff-card";
        } else {
            return "redirect:/staff/staff-list";
        }
    }

    @RolesAllowed("ADMIN")
    @PostMapping("/staff/edit-staff/{staffId}")
    public String updateStaff(@PathVariable("staffId") int staffId,
            @ModelAttribute("staff") @Validated Staff updatedStaff, BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingValidator.validate(bindingResult, redirectAttributes)) {
            try {
                Staff resultStaff = staffService.updateStaffById(staffId, updatedStaff);

                if (resultStaff != null) {
                    redirectAttributes.addFlashAttribute(Message.SUCCESS, Message.UPDATE_SUCCESS);
                } else {
                    redirectAttributes.addFlashAttribute(Message.ERROR, Message.FAILURE);
                }
            } catch (NoSuchElementException | IllegalStateException ex) {
                redirectAttributes.addFlashAttribute(Message.ERROR, ex.getLocalizedMessage());
            }
        }
        return "redirect:/staff/staff-card/" + staffId;
    }

}
