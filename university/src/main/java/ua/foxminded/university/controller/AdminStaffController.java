package ua.foxminded.university.controller;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

	@GetMapping("/admin/staff/search-result")
	public String searchStaffAsAdmin(@RequestParam("searchType") String searchType,
			@RequestParam(required = false) Integer staffId, @RequestParam(required = false) String firstName,
			@RequestParam(required = false) String lastName, Model model) {
		List<Staff> staffList;

		if ("staff".equals(searchType)) {
			Optional<Staff> optionalStaff = staffService.findStaffById(staffId);
			staffList = optionalStaff.map(Collections::singletonList).orElse(Collections.emptyList());
		} else if ("firstNameAndLastName".equals(searchType)) {
			Optional<Staff> optionalStaff = staffService.findStaffByName(firstName, lastName);
			staffList = optionalStaff.map(Collections::singletonList).orElse(Collections.emptyList());
		} else {
			return "error";
		}
		model.addAttribute("staff", staffList);
		return "admin/staff/edit-staff-list";
	}
}
