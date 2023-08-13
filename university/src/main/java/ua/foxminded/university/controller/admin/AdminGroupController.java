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
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ua.foxminded.university.dao.entities.Faculty;
import ua.foxminded.university.dao.entities.Group;
import ua.foxminded.university.dao.service.FacultyService;
import ua.foxminded.university.dao.service.GroupService;

@Controller
public class AdminGroupController {

	@Autowired
	private GroupService groupService;

	@Autowired
	private FacultyService facultyService;

	@GetMapping("/admin/group/edit-group-list")
	public String getAllGroupListAsAdmin(Model model) {
		List<Group> groups = groupService.getAllGroups();

		model.addAttribute("groups", groups);
		return "admin/group/edit-group-list";
	}

	@PostMapping("admin/group/delete/{groupId}")
	public String deleteGroup(@PathVariable int groupId, RedirectAttributes redirectAttributes,
			HttpServletRequest request) {
		try {
			groupService.deleteGroupById(groupId);
			redirectAttributes.addFlashAttribute("successMessage", "Group was deleted!");
		} catch (NoSuchElementException ex) {
			redirectAttributes.addFlashAttribute("errorMessage", ex.getLocalizedMessage());
		}
		String referrer = request.getHeader("referer");

		if (referrer == null || referrer.isEmpty()) {
			return "redirect:/admin/group/edit-group-list";
		}
		return "redirect:" + referrer;
	}

	@GetMapping("/admin/group/create-group")
	public String showCreateGroupForm(Model model) {
		List<Faculty> faculties = facultyService.getAllFaculties();

		model.addAttribute("faculties", faculties);
		return "admin/group/create-group";
	}

	@PostMapping("/admin/group/create-group")
	public String createGroup(@ModelAttribute("group") @Validated Group group, BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			for (FieldError error : bindingResult.getFieldErrors()) {
				redirectAttributes.addFlashAttribute(error.getField() + "Error", error.getDefaultMessage());
			}
			return "redirect:/admin/group/create-group";
		}

		try {
			int createdGroup = groupService.createGroup(group);

			if (createdGroup != group.getId()) {
				redirectAttributes.addFlashAttribute("errorMessage", "Failed to create Group");
			} else {
				redirectAttributes.addFlashAttribute("successMessage", "Group created successfully");
			}
		} catch (IllegalStateException ex) {
			redirectAttributes.addFlashAttribute("errorMessage", ex.getLocalizedMessage());
		}
		return "redirect:/admin/group/create-group";
	}

	@GetMapping("/admin/group/search-result")
	public String searchGroupAsAdmin(@RequestParam("searchType") String searchType,
			@RequestParam(required = false) Integer groupId, @RequestParam(required = false) String groupName,
			@RequestParam(required = false) String facultyName, Model model) {
		List<Group> groupList;

		if ("group".equals(searchType)) {
			Optional<Group> optionalGroup = groupService.findGroupById(groupId);
			groupList = optionalGroup.map(Collections::singletonList).orElse(Collections.emptyList());
		} else if ("groupName".equals(searchType)) {
			groupList = groupService.findGroupByGroupName(groupName);
		} else if ("faculty".equals(searchType)) {
			groupList = groupService.findAllByFacultyName(facultyName);
		} else {
			return "error";
		}
		model.addAttribute("groups", groupList);
		return "admin/group/edit-group-list";
	}

	@GetMapping("/admin/group/group-card/{groupId}")
	public String openGroupCard(@PathVariable int groupId, Model model) {
		Optional<Group> optionalGroup = groupService.findGroupById(groupId);

		if (optionalGroup.isPresent()) {
			Group group = optionalGroup.get();
			model.addAttribute("group", group);
			return "admin/group/group-card";
		} else {
			return "redirect:/admin/group/edit-group-list";
		}
	}
}
