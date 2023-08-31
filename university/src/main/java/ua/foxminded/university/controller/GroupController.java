package ua.foxminded.university.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import javax.annotation.security.RolesAllowed;
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
import ua.foxminded.university.dao.entities.Faculty;
import ua.foxminded.university.dao.entities.Group;
import ua.foxminded.university.dao.service.FacultyService;
import ua.foxminded.university.dao.service.GroupService;
import ua.foxminded.university.validation.ControllerBindingValidator;
import ua.foxminded.university.validation.Message;

@Controller
public class GroupController {

    @Autowired
    private GroupService groupService;

    @Autowired
    private FacultyService facultyService;

    @Autowired
    private ControllerBindingValidator bindingValidator;

    @GetMapping("/group/group-list")
    public String getAllGroupList(Model model) {
        List<Group> groups = groupService.getAllGroups();

        model.addAttribute("groups", groups);
        return "group/group-list";
    }

    @RolesAllowed("ADMIN")
    @PostMapping("/group/delete/{groupId}")
    public String deleteGroup(@PathVariable int groupId, RedirectAttributes redirectAttributes,
            HttpServletRequest request) {
        try {
            groupService.deleteGroupById(groupId);
            redirectAttributes.addFlashAttribute(Message.SUCCESS, Message.DELETE_SUCCESS);
        } catch (NoSuchElementException ex) {
            redirectAttributes.addFlashAttribute(Message.ERROR, ex.getLocalizedMessage());
        }
        String referrer = request.getHeader("referer");

        if (referrer == null || referrer.isEmpty()) {
            return "redirect:/group/group-list";
        }
        return "redirect:" + referrer;
    }

    @RolesAllowed("ADMIN")
    @GetMapping("/group/create-group")
    public String showCreateGroupForm(Model model) {
        List<Faculty> faculties = facultyService.getAllFaculties();

        model.addAttribute("faculties", faculties);
        return "group/create-group";
    }

    @RolesAllowed("ADMIN")
    @PostMapping("/group/create-group")
    public String createGroup(@ModelAttribute("group") @Validated Group group, BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingValidator.validate(bindingResult, redirectAttributes)) {
            try {
                int createdGroup = groupService.createGroup(group);

                if (createdGroup != group.getId()) {
                    redirectAttributes.addFlashAttribute(Message.ERROR, Message.FAILURE);
                } else {
                    redirectAttributes.addFlashAttribute(Message.SUCCESS, Message.CREATE_SUCCESS);
                }
            } catch (IllegalStateException ex) {
                redirectAttributes.addFlashAttribute(Message.ERROR, ex.getLocalizedMessage());
            }
        }
        return "redirect:/group/create-group";
    }

    @GetMapping("/group/search-result")
    public String searchGroup(@RequestParam("searchType") String searchType,
            @RequestParam(required = false) Integer groupId, @RequestParam(required = false) String groupName,
            @RequestParam(required = false) String facultyName, Model model) {
        List<Group> groupList = new ArrayList<>();

        if ("group".equals(searchType)) {
            Optional<Group> optionalGroup = groupService.findGroupById(groupId);
            groupList = optionalGroup.map(Collections::singletonList).orElse(Collections.emptyList());
        } else if ("groupName".equals(searchType)) {
            groupList = groupService.findGroupByGroupName(groupName);
        } else if ("faculty".equals(searchType)) {
            groupList = groupService.findAllByFacultyName(facultyName);
        }
        model.addAttribute("groups", groupList);
        return "group/group-list";
    }

    @RolesAllowed("ADMIN")
    @GetMapping("/group/group-card/{groupId}")
    public String openGroupCard(@PathVariable int groupId, Model model) {
        Optional<Group> optionalGroup = groupService.findGroupById(groupId);
        List<Faculty> faculties = facultyService.getAllFaculties();

        if (optionalGroup.isPresent()) {
            Group group = optionalGroup.get();
            model.addAttribute("group", group);
            model.addAttribute("faculties", faculties);
            return "group/group-card";
        } else {
            return "redirect:/group/group-list";
        }
    }

    @RolesAllowed("ADMIN")
    @PostMapping("/group/edit-group/{groupId}")
    public String updateGroup(@PathVariable("groupId") int groupId,
            @ModelAttribute("group") @Validated Group updatedGroup, BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingValidator.validate(bindingResult, redirectAttributes)) {
            try {
                Group resultGroup = groupService.updateGroupById(groupId, updatedGroup);

                if (resultGroup != null) {
                    redirectAttributes.addFlashAttribute(Message.SUCCESS, Message.UPDATE_SUCCESS);
                } else {
                    redirectAttributes.addFlashAttribute(Message.ERROR, Message.FAILURE);
                }
            } catch (NoSuchElementException | IllegalStateException ex) {
                redirectAttributes.addFlashAttribute(Message.ERROR, ex.getLocalizedMessage());
            }
        }
        return "redirect:/group/group-card/" + groupId;
    }
}
