package ua.foxminded.university.controller.admin;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ua.foxminded.university.dao.entities.Admin;
import ua.foxminded.university.dao.service.AdminService;
import ua.foxminded.university.validation.ControllerBindingValidator;

@Controller
public class AdminController {

	@Autowired
	private AdminService adminService;

	@Autowired
	private ControllerBindingValidator bindingValidator;

	@GetMapping("/admin/main")
	public String adminMainPage() {
		return "admin/main";
	}

	@GetMapping("/admin/edit-admin-list")
	public String getAllAdminListAsAdmin(Model model) {
		List<Admin> admins = adminService.getAllAdmins();

		model.addAttribute("admins", admins);
		return "admin/edit-admin-list";
	}

	@PostMapping("/admin/delete/{adminId}")
	public String deleteAdmin(@PathVariable int adminId, RedirectAttributes redirectAttributes,
			HttpServletRequest request) {
		try {
			adminService.deleteAdminById(adminId);
			redirectAttributes.addFlashAttribute("successMessage", "Admin was deleted!");
		} catch (NoSuchElementException ex) {
			redirectAttributes.addFlashAttribute("errorMessage", ex.getLocalizedMessage());
		}
		String referrer = request.getHeader("referer");

		if (referrer == null || referrer.isEmpty()) {
			return "redirect:/admin/edit-admin-list";
		}
		return "redirect:" + referrer;
	}

	@GetMapping("/admin/admin-card/{adminId}")
	public String openAdminCard(@PathVariable int adminId, Model model) {
		Optional<Admin> optionalAdmin = adminService.findAdminById(adminId);

		if (optionalAdmin.isPresent()) {
			Admin admin = optionalAdmin.get();
			model.addAttribute("admin", admin);
			return "admin/admin-card";
		} else {
			return "redirect:/admin/edit-admin-list";
		}
	}

	@GetMapping("/admin/create-admin")
	public String showCreateAdminForm() {
		return "admin/create-admin";
	}

	@PostMapping("/admin/create-admin")
	public String createAdmin(@ModelAttribute("admin") @Validated Admin admin, BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {
		if (bindingValidator.validate(bindingResult, redirectAttributes)) {
			try {
				int createdAdmin = adminService.createAdmin(admin);

				if (createdAdmin != admin.getId()) {
					redirectAttributes.addFlashAttribute("errorMessage", "Failed to create the admin");
				} else {
					redirectAttributes.addFlashAttribute("successMessage", "Admin created successfully");
				}
			} catch (IllegalStateException ex) {
				redirectAttributes.addFlashAttribute("errorMessage", ex.getLocalizedMessage());
			}
			return "redirect:/admin/create-admin";
		} else {
			return "redirect:/admin/create-admin";
		}
	}

	@PostMapping("/admin/edit-admin/{adminId}")
	public String updateAdmin(@PathVariable("adminId") int adminId,
			@ModelAttribute("admin") @Validated Admin updatedAdmin, BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {
		if (bindingValidator.validate(bindingResult, redirectAttributes)) {
			try {
				Admin resultAdmin = adminService.updateAdminById(adminId, updatedAdmin);

				if (resultAdmin != null) {
					redirectAttributes.addFlashAttribute("successMessage", "Admin updated successfully");
				} else {
					redirectAttributes.addFlashAttribute("errorMessage", "Failed to update Admin");
				}
			} catch (NoSuchElementException ex) {
				redirectAttributes.addFlashAttribute("errorMessage", "Admin not found");
			}
		} else {
			return "redirect:/admin/admin-card/" + adminId;
		}
		return "redirect:/admin/admin-card/" + adminId;
	}

}