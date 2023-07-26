package ua.foxminded.university.controller;

import java.util.ArrayList;
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
import ua.foxminded.university.dao.entities.Department;
import ua.foxminded.university.dao.entities.Faculty;
import ua.foxminded.university.dao.entities.Group;
import ua.foxminded.university.dao.service.DepartmentService;
import ua.foxminded.university.dao.service.FacultyService;
import ua.foxminded.university.dao.service.GroupService;

@Controller
public class AdminFacultyController {

	@Autowired
	private FacultyService facultyService;

	@Autowired
	private GroupService groupService;

	@Autowired
	private DepartmentService departmentService;

	@GetMapping("/admin/faculty/edit-faculty-list")
	public String getAllFacultyListAsAdmin(Model model) {
		List<Faculty> faculties = facultyService.getAllFaculties();
		List<Group> groups = new ArrayList<>();
		List<Department> departments = new ArrayList<>();

		for (Faculty faculty : faculties) {
			groups = groupService.findAllByFaculty(faculty);
			departments = departmentService.findAllByFaculty(faculty);
		}
		model.addAttribute("faculties", faculties);
		model.addAttribute("groups", groups);
		model.addAttribute("departments", departments);
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
