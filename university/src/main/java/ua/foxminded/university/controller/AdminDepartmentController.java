package ua.foxminded.university.controller;

import java.util.List;
import java.util.NoSuchElementException;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ua.foxminded.university.dao.entities.Department;
import ua.foxminded.university.dao.entities.Faculty;
import ua.foxminded.university.dao.service.DepartmentService;
import ua.foxminded.university.dao.service.FacultyService;

@Controller
public class AdminDepartmentController {

	@Autowired
	private DepartmentService departmentService;

	@Autowired
	private FacultyService facultyService;

	@GetMapping("/admin/department/edit-department-list")
	public String getAllDepartmentListAsAdmin(Model model) {
		List<Department> departments = departmentService.getAllDepartments();

		model.addAttribute("departments", departments);
		return "admin/department/edit-department-list";
	}

	@PostMapping("admin/department/delete/{departmentId}")
	public String deleteDepartment(@PathVariable int departmentId, RedirectAttributes redirectAttributes,
			HttpServletRequest request) {
		try {
			departmentService.deleteDepartmentById(departmentId);
			redirectAttributes.addFlashAttribute("successMessage", "Department was deleted!");
		} catch (NoSuchElementException ex) {
			redirectAttributes.addFlashAttribute("errorMessage", ex.getLocalizedMessage());
		}
		String referrer = request.getHeader("referer");

		if (referrer == null || referrer.isEmpty()) {
			return "redirect:/admin/department/edit-department-list";
		}
		return "redirect:" + referrer;
	}

	@GetMapping("/admin/department/create-department")
	public String showCreateDepartmentForm(Model model) {
		List<Faculty> faculties = facultyService.getAllFaculties();

		model.addAttribute("faculties", faculties);
		return "admin/department/create-department";
	}

	@PostMapping("/admin/department/create-department")
	public String createDepartment(@ModelAttribute("department") @Validated Department department,
			BindingResult bindingResult, RedirectAttributes redirectAttributes) {
		try {
			if (bindingResult.hasErrors()) {
				for (FieldError error : bindingResult.getFieldErrors()) {
					redirectAttributes.addFlashAttribute(error.getField() + "Error", error.getDefaultMessage());
				}
				return "redirect:/admin/department/create-department";
			}

			int createdDepartment = departmentService.createDepartment(department);

			if (createdDepartment != department.getId()) {
				redirectAttributes.addFlashAttribute("errorMessage", "Failed to create Department");
			} else {
				redirectAttributes.addFlashAttribute("successMessage", "Department created successfully");
			}
		} catch (IllegalStateException ex) {
			redirectAttributes.addFlashAttribute("errorMessage", ex.getLocalizedMessage());
		}
		return "redirect:/admin/department/create-department";
	}
}
