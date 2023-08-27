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
import ua.foxminded.university.dao.entities.Faculty;
import ua.foxminded.university.dao.service.FacultyService;
import ua.foxminded.university.validation.ControllerBindingValidator;

@Controller
public class AdminFacultyController {

	@Autowired
	private FacultyService facultyService;

	@Autowired
	private ControllerBindingValidator bindingValidator;

	@GetMapping("/faculty/faculty-list")
	public String getAllFacultyList(Model model) {
		List<Faculty> faculties = facultyService.getAllFaculties();

		model.addAttribute("faculties", faculties);
		return "faculty/faculty-list";
	}

	@PostMapping("/faculty/delete/{facultyId}")
	public String deleteFaculty(@PathVariable int facultyId, RedirectAttributes redirectAttributes,
			HttpServletRequest request) {
		try {
			facultyService.deleteFacultyById(facultyId);
			redirectAttributes.addFlashAttribute("successMessage", "Faculty was deleted!");
		} catch (NoSuchElementException ex) {
			redirectAttributes.addFlashAttribute("errorMessage", ex.getLocalizedMessage());
		}
		String referrer = request.getHeader("referer");

		if (referrer == null || referrer.isEmpty()) {
			return "redirect:/faculty/faculty-list";
		}
		return "redirect:" + referrer;
	}

	@GetMapping("/faculty/create-faculty")
	public String showCreateFacultyForm() {
		return "faculty/create-faculty";
	}

	@PostMapping("/faculty/create-faculty")
	public String createFaculty(@ModelAttribute("faculty") @Validated Faculty faculty, BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {
		if (bindingValidator.validate(bindingResult, redirectAttributes)) {
			try {
				int createdFaculty = facultyService.createFaculty(faculty);

				if (createdFaculty != faculty.getId()) {
					redirectAttributes.addFlashAttribute("errorMessage", "Failed to create the Faculty");
				} else {
					redirectAttributes.addFlashAttribute("successMessage", "Faculty created successfully");
				}
			} catch (IllegalStateException ex) {
				redirectAttributes.addFlashAttribute("errorMessage", ex.getLocalizedMessage());
			}
			return "redirect:/faculty/create-faculty";
		} else {
			return "redirect:/faculty/create-faculty";
		}
	}

	@GetMapping("/faculty/faculty-card/{facultyId}")
	public String openFacultyCard(@PathVariable int facultyId, Model model) {
		Optional<Faculty> optionalFaculty = facultyService.findFacultyById(facultyId);

		if (optionalFaculty.isPresent()) {
			Faculty faculty = optionalFaculty.get();
			model.addAttribute("faculty", faculty);
			return "faculty/faculty-card";
		} else {
			return "redirect:/faculty/faculty-list";
		}
	}

	@PostMapping("/faculty/edit-faculty/{facultyId}")
	public String updateFaculty(@PathVariable("facultyId") int facultyId,
			@ModelAttribute("faculty") @Validated Faculty updatedFaculty, BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {
		if (bindingValidator.validate(bindingResult, redirectAttributes)) {
			try {
				Faculty resultFaculty = facultyService.updateFacultyById(facultyId, updatedFaculty);

				if (resultFaculty != null) {
					redirectAttributes.addFlashAttribute("successMessage", "Faculty updated successfully");
				} else {
					redirectAttributes.addFlashAttribute("errorMessage", "Failed to update the Faculty");
				}
			} catch (NoSuchElementException ex) {
				redirectAttributes.addFlashAttribute("errorMessage", "Faculty not found");
			}
		} else {
			return "redirect:/faculty/faculty-card/" + facultyId;
		}
		return "redirect:/faculty/faculty-card/" + facultyId;
	}

}
