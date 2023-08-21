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
import ua.foxminded.university.dao.entities.Department;
import ua.foxminded.university.dao.entities.Faculty;
import ua.foxminded.university.dao.service.DepartmentService;
import ua.foxminded.university.dao.service.FacultyService;
import ua.foxminded.university.validation.ControllerBindingValidator;

@Controller
public class AdminDepartmentController {

	@Autowired
	private DepartmentService departmentService;

	@Autowired
	private FacultyService facultyService;

	@Autowired
	private ControllerBindingValidator bindingValidator;

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
		if (bindingValidator.validate(bindingResult, redirectAttributes)) {
			try {
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
		} else {
			return "redirect:/admin/department/create-department";
		}
	}

	@GetMapping("/admin/department/search-result")
	public String searchDepartmentAsAdmin(@RequestParam("searchType") String searchType,
			@RequestParam(required = false) Integer departmentId, @RequestParam(required = false) String name,
			@RequestParam(required = false) String facultyName, Model model) {
		List<Department> departmentList;

		if ("department".equals(searchType)) {
			Optional<Department> optionalDepartment = departmentService.findDepartmentById(departmentId);
			departmentList = optionalDepartment.map(Collections::singletonList).orElse(Collections.emptyList());
		} else if ("name".equals(searchType)) {
			departmentList = departmentService.findDepartmentByName(name);
		} else if ("faculty".equals(searchType)) {
			departmentList = departmentService.findAllByFacultyName(facultyName);
		} else {
			return "error";
		}
		model.addAttribute("departments", departmentList);
		return "admin/department/edit-department-list";
	}

	@GetMapping("/admin/department/department-card/{departmentId}")
	public String openDepartmentCard(@PathVariable int departmentId, Model model) {
		Optional<Department> optionalDepartment = departmentService.findDepartmentById(departmentId);

		if (optionalDepartment.isPresent()) {
			Department department = optionalDepartment.get();
			model.addAttribute("department", department);
			return "admin/department/department-card";
		} else {
			return "redirect:/admin/department/edit-department-list";
		}
	}

	@PostMapping("/admin/department/edit-department/{departmentId}")
	public String updateDepartment(@PathVariable("departmentId") int departmentId,
			@ModelAttribute("department") @Validated Department updatedDepartment, BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {
		if (bindingValidator.validate(bindingResult, redirectAttributes)) {
			try {
				Department resultDepartment = departmentService.updateDepartmentById(departmentId, updatedDepartment);

				if (resultDepartment != null) {
					redirectAttributes.addFlashAttribute("successMessage", "Department updated successfully");
				} else {
					redirectAttributes.addFlashAttribute("errorMessage", "Failed to update Department");
				}
			} catch (NoSuchElementException ex) {
				redirectAttributes.addFlashAttribute("errorMessage", "Department not found");
			}
		} else {
			return "redirect:/admin/department/department-card/" + departmentId;
		}
		return "redirect:/admin/department/department-card/" + departmentId;
	}
}
