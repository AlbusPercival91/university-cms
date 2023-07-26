package ua.foxminded.university.controller;

import java.util.List;
import java.util.NoSuchElementException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ua.foxminded.university.dao.entities.Staff;
import ua.foxminded.university.dao.service.StaffService;

@Controller
public class AdminStaffController {

	@Autowired
	private StaffService staffService;

	@GetMapping("/admin/staff/edit-staff-list")
	public String getAllStaffListAsAdmin(Model model) {
		List<Staff> staff = staffService.getAllStaff();

		model.addAttribute("staff", staff);
		return "admin/staff/edit-staff-list";
	}

	@PostMapping("admin/staff/delete/{staffId}")
	public String deleteStaff(@PathVariable int staffId, RedirectAttributes redirectAttributes,
			HttpServletRequest request) {
		try {
			staffService.deleteStaffById(staffId);
			redirectAttributes.addFlashAttribute("successMessage", "Staff member was deleted!");
		} catch (NoSuchElementException ex) {
			redirectAttributes.addFlashAttribute("errorMessage", ex.getLocalizedMessage());
		}
		String referrer = request.getHeader("referer");

		if (referrer == null || referrer.isEmpty()) {
			return "redirect:/admin/staff/edit-staff-list";
		}
		return "redirect:" + referrer;
	}
}
