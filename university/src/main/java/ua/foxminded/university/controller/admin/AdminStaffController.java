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
import ua.foxminded.university.dao.entities.Staff;
import ua.foxminded.university.dao.service.StaffService;
import ua.foxminded.university.validation.ControllerBindingValidator;

@Controller
public class AdminStaffController {

	@Autowired
	private StaffService staffService;

	@Autowired
	private ControllerBindingValidator bindingValidator;

	@GetMapping("/admin/staff/create-staff")
	public String showCreateStaffForm() {
		return "admin/staff/create-staff";
	}

	@PostMapping("/admin/staff/create-staff")
	public String createStaff(@ModelAttribute("staff") @Validated Staff staff, BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {
		if (bindingValidator.validate(bindingResult, redirectAttributes)) {
			try {
				int createdStaff = staffService.createStaff(staff);

				if (createdStaff != staff.getId()) {
					redirectAttributes.addFlashAttribute("errorMessage", "Failed to create the Staff member");
				} else {
					redirectAttributes.addFlashAttribute("successMessage", "Staff created successfully");
				}
			} catch (IllegalStateException ex) {
				redirectAttributes.addFlashAttribute("errorMessage", ex.getLocalizedMessage());
			}
			return "redirect:/admin/staff/create-staff";
		} else {
			return "redirect:/admin/staff/create-staff";
		}
	}

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
			@RequestParam(required = false) String lastName, @RequestParam(required = false) String position,
			Model model) {
		List<Staff> staffList;

		if ("staff".equals(searchType)) {
			Optional<Staff> optionalStaff = staffService.findStaffById(staffId);
			staffList = optionalStaff.map(Collections::singletonList).orElse(Collections.emptyList());
		} else if ("firstNameAndLastName".equals(searchType)) {
			staffList = staffService.findStaffByName(firstName, lastName);
		} else if ("position".equals(searchType)) {
			staffList = staffService.findStaffByPosition(position);
		} else {
			return "error";
		}
		model.addAttribute("staff", staffList);
		return "admin/staff/edit-staff-list";
	}

	@GetMapping("/admin/staff/staff-card/{staffId}")
	public String openStaffCard(@PathVariable int staffId, Model model) {
		Optional<Staff> optionalStaff = staffService.findStaffById(staffId);

		if (optionalStaff.isPresent()) {
			Staff staff = optionalStaff.get();
			model.addAttribute("staff", staff);
			return "admin/staff/staff-card";
		} else {
			return "redirect:/admin/staff/edit-staff-list";
		}
	}

	@PostMapping("/admin/staff/edit-staff/{staffId}")
	public String updateStaff(@PathVariable("staffId") int staffId,
			@ModelAttribute("staff") @Validated Staff updatedStaff, BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {
		if (bindingValidator.validate(bindingResult, redirectAttributes)) {
			try {
				Staff resultStaff = staffService.updateStaffById(staffId, updatedStaff);

				if (resultStaff != null) {
					redirectAttributes.addFlashAttribute("successMessage", "Staff updated successfully");
				} else {
					redirectAttributes.addFlashAttribute("errorMessage", "Failed to update Staff");
				}
			} catch (NoSuchElementException | IllegalStateException ex) {
				redirectAttributes.addFlashAttribute("errorMessage", ex.getLocalizedMessage());
			}
		} else {
			return "redirect:/admin/staff/staff-card/" + staffId;
		}
		return "redirect:/admin/staff/staff-card/" + staffId;
	}

}
