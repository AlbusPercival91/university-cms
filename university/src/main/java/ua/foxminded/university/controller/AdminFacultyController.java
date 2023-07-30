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
import ua.foxminded.university.dao.entities.Faculty;
import ua.foxminded.university.dao.service.FacultyService;

@Controller
public class AdminFacultyController {

	@Autowired
	private FacultyService facultyService;

	@GetMapping("/admin/faculty/edit-faculty-list")
	public String getAllFacultyListAsAdmin(Model model) {
		List<Faculty> faculties = facultyService.getAllFaculties();

		model.addAttribute("faculties", faculties);
		return "admin/faculty/edit-faculty-list";
	}

	@PostMapping("admin/faculty/delete/{facultyId}")
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
			return "redirect:/admin/faculty/edit-faculty-list";
		}
		return "redirect:" + referrer;
	}

}
