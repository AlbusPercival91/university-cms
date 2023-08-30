package ua.foxminded.university.controller;

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
import ua.foxminded.university.dao.entities.Admin;
import ua.foxminded.university.dao.service.AdminService;
import ua.foxminded.university.validation.ControllerBindingValidator;
import ua.foxminded.university.validation.Message;

@Controller
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private ControllerBindingValidator bindingValidator;

    @GetMapping("/admin/main")
    public String adminDashboard(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            Optional<Admin> admin = adminService.findAdminByEmail(email);
            if (admin.isPresent()) {
                model.addAttribute("admin", admin.get());
                return "admin/main";
            }
        }
        return "redirect:/login";
    }

    @PostMapping("/admin/update-personal/{adminId}")
    public String updatePersonalData(@PathVariable("adminId") int adminId,
            @ModelAttribute("admin") @Validated Admin updatedAdmin, BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingValidator.validate(bindingResult, redirectAttributes)) {
            try {
                Admin resultAdmin = adminService.updateAdminById(adminId, updatedAdmin);

                if (resultAdmin != null) {
                    redirectAttributes.addFlashAttribute(Message.SUCCESS, Message.UPDATE_SUCCESS);
                } else {
                    redirectAttributes.addFlashAttribute(Message.ERROR, Message.FAILURE);
                }
            } catch (NoSuchElementException | IllegalStateException ex) {
                redirectAttributes.addFlashAttribute("errorMessage", ex.getLocalizedMessage());
            }
        }
        return "redirect:/admin/main";
    }

    @PostMapping("/admin/update-password")
    public String updatePassword(@RequestParam int adminId, @RequestParam String oldPassword,
            @RequestParam String newPassword, RedirectAttributes redirectAttributes) {
        try {
            Admin resultAdmin = adminService.changeAdminPasswordById(adminId, oldPassword, newPassword);
            if (resultAdmin != null) {
                redirectAttributes.addFlashAttribute(Message.SUCCESS, Message.UPDATE_SUCCESS);
            } else {
                redirectAttributes.addFlashAttribute(Message.ERROR, Message.FAILURE);
            }
        } catch (NoSuchElementException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getLocalizedMessage());
        }
        return "redirect:/admin/main";
    }

    @RolesAllowed("ADMIN")
    @GetMapping("/admin/admin-list")
    public String getAllAdminList(Model model) {
        List<Admin> admins = adminService.getAllAdmins();

        model.addAttribute("admins", admins);
        return "admin/admin-list";
    }

    @RolesAllowed("ADMIN")
    @PostMapping("/admin/delete/{adminId}")
    public String deleteAdmin(@PathVariable int adminId, RedirectAttributes redirectAttributes,
            HttpServletRequest request) {
        try {
            adminService.deleteAdminById(adminId);
            redirectAttributes.addFlashAttribute(Message.SUCCESS, Message.DELETE_SUCCESS);
        } catch (NoSuchElementException ex) {
            redirectAttributes.addFlashAttribute(Message.ERROR, ex.getLocalizedMessage());
        }
        String referrer = request.getHeader("referer");

        if (referrer == null || referrer.isEmpty()) {
            return "redirect:/admin/admin-list";
        }
        return "redirect:" + referrer;
    }

    @RolesAllowed("ADMIN")
    @GetMapping("/admin/admin-card/{adminId}")
    public String openAdminCard(@PathVariable int adminId, Model model) {
        Optional<Admin> optionalAdmin = adminService.findAdminById(adminId);

        if (optionalAdmin.isPresent()) {
            Admin admin = optionalAdmin.get();
            model.addAttribute("admin", admin);
            return "admin/admin-card";
        } else {
            return "redirect:/admin/admin-list";
        }
    }

    @RolesAllowed("ADMIN")
    @GetMapping("/admin/create-admin")
    public String showCreateAdminForm() {
        return "admin/create-admin";
    }

    @RolesAllowed("ADMIN")
    @PostMapping("/admin/create-admin")
    public String createAdmin(@ModelAttribute("admin") @Validated Admin admin, BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingValidator.validate(bindingResult, redirectAttributes)) {
            try {
                int createdAdmin = adminService.createAdmin(admin);

                if (createdAdmin != admin.getId()) {
                    redirectAttributes.addFlashAttribute(Message.ERROR, Message.FAILURE);
                } else {
                    redirectAttributes.addFlashAttribute(Message.SUCCESS, Message.CREATE_SUCCESS);
                }
            } catch (IllegalStateException ex) {
                redirectAttributes.addFlashAttribute("errorMessage", ex.getLocalizedMessage());
            }
        }
        return "redirect:/admin/create-admin";
    }

    @RolesAllowed("ADMIN")
    @PostMapping("/admin/edit-admin/{adminId}")
    public String updateAdmin(@PathVariable("adminId") int adminId,
            @ModelAttribute("admin") @Validated Admin updatedAdmin, BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingValidator.validate(bindingResult, redirectAttributes)) {
            try {
                Admin resultAdmin = adminService.updateAdminById(adminId, updatedAdmin);

                if (resultAdmin != null) {
                    redirectAttributes.addFlashAttribute(Message.SUCCESS, Message.UPDATE_SUCCESS);
                } else {
                    redirectAttributes.addFlashAttribute(Message.ERROR, Message.FAILURE);
                }
            } catch (NoSuchElementException | IllegalStateException ex) {
                redirectAttributes.addFlashAttribute(Message.ERROR, ex.getLocalizedMessage());
            }
        }
        return "redirect:/admin/admin-card/" + adminId;
    }

}
