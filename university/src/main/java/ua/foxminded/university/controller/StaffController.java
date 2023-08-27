package ua.foxminded.university.controller;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
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
import ua.foxminded.university.dao.entities.Staff;
import ua.foxminded.university.dao.service.StaffService;
import ua.foxminded.university.validation.ControllerBindingValidator;

@Controller
public class StaffController {

	@Autowired
	private StaffService staffService;

	@Autowired
	private ControllerBindingValidator bindingValidator;

	@GetMapping("/staff/main")
	public String staffMainPage(Model model) {
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
					redirectAttributes.addFlashAttribute("successMessage", "Data updated successfully");
				} else {
					redirectAttributes.addFlashAttribute("errorMessage", "Failed to update Data");
				}
			} catch (NoSuchElementException | IllegalStateException ex) {
				redirectAttributes.addFlashAttribute("errorMessage", ex.getLocalizedMessage());
			}
		} else {
			return "redirect:/staff/main";
		}
		return "redirect:/staff/main";
	}

	@PostMapping("/staff/update-password")
	public String updatePassword(@RequestParam int staffId, @RequestParam String oldPassword,
			@RequestParam String newPassword, RedirectAttributes redirectAttributes) {
		try {
			Staff resultStaff = staffService.changeStaffPasswordById(staffId, oldPassword, newPassword);
			if (resultStaff != null) {
				redirectAttributes.addFlashAttribute("successMessage", "Password changed successfully");
			} else {
				redirectAttributes.addFlashAttribute("errorMessage", "Failed to change Password");
			}
		} catch (NoSuchElementException | IllegalStateException ex) {
			redirectAttributes.addFlashAttribute("errorMessage", ex.getLocalizedMessage());
		}
		return "redirect:/staff/main";
	}

	@GetMapping("/staff/create-staff")
	public String showCreateStaffForm() {
		return "staff/create-staff";
	}

	@PostMapping("/staff/create-staff")
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
			return "redirect:/staff/create-staff";
		} else {
			return "redirect:/staff/create-staff";
		}
	}

	@GetMapping("/staff/staff-list")
	public String getAllStaffList(Model model) {
		List<Staff> staff = staffService.getAllStaff();

		model.addAttribute("staff", staff);
		return "staff/staff-list";
	}

	@PostMapping("/staff/delete/{staffId}")
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
			return "redirect:/staff/staff-list";
		}
		return "redirect:" + referrer;
	}

	@GetMapping("/staff/search-result")
	public String searchStaff(@RequestParam("searchType") String searchType,
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
		return "staff/staff-list";
	}

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

	@PostMapping("/staff/edit-staff/{staffId}")
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
			return "redirect:/staff/staff-card/" + staffId;
		}
		return "redirect:/staff/staff-card/" + staffId;
	}

}
