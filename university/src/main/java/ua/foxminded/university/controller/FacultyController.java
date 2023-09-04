package ua.foxminded.university.controller;

import java.time.LocalDateTime;
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
import ua.foxminded.university.dao.service.AlertService;
import ua.foxminded.university.dao.service.FacultyService;
import ua.foxminded.university.validation.ControllerBindingValidator;
import ua.foxminded.university.validation.Message;

@Controller
public class FacultyController {

    @Autowired
    private FacultyService facultyService;

    @Autowired
    private AlertService alertService;

    @Autowired
    private ControllerBindingValidator bindingValidator;

    @PostMapping("/faculty/send-alert/{facultyId}")
    public String sendFacultyAlert(@PathVariable int facultyId, @RequestParam String alertMessage,
            RedirectAttributes redirectAttributes) {
        try {
            alertService.createFacultyAlert(LocalDateTime.now(), facultyId, alertMessage);

            if (alertMessage != null) {
                redirectAttributes.addFlashAttribute(Message.SUCCESS, Message.ALERT_SUCCESS);
            }
        } catch (NoSuchElementException ex) {
            redirectAttributes.addFlashAttribute(Message.ERROR, ex.getLocalizedMessage());
        }
        return "redirect:/faculty/faculty-card/" + facultyId;
    }

    @GetMapping("/faculty/faculty-list")
    public String getAllFacultyList(Model model) {
        List<Faculty> faculties = facultyService.getAllFaculties();

        model.addAttribute("faculties", faculties);
        return "faculty/faculty-list";
    }

    @RolesAllowed("ADMIN")
    @PostMapping("/faculty/delete/{facultyId}")
    public String deleteFaculty(@PathVariable int facultyId, RedirectAttributes redirectAttributes,
            HttpServletRequest request) {
        try {
            facultyService.deleteFacultyById(facultyId);
            redirectAttributes.addFlashAttribute(Message.SUCCESS, Message.DELETE_SUCCESS);
        } catch (NoSuchElementException ex) {
            redirectAttributes.addFlashAttribute(Message.ERROR, ex.getLocalizedMessage());
        }
        String referrer = request.getHeader("referer");

        if (referrer == null || referrer.isEmpty()) {
            return "redirect:/faculty/faculty-list";
        }
        return "redirect:" + referrer;
    }

    @RolesAllowed("ADMIN")
    @GetMapping("/faculty/create-faculty")
    public String showCreateFacultyForm() {
        return "faculty/create-faculty";
    }

    @RolesAllowed("ADMIN")
    @PostMapping("/faculty/create-faculty")
    public String createFaculty(@ModelAttribute("faculty") @Validated Faculty faculty, BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingValidator.validate(bindingResult, redirectAttributes)) {
            try {
                int createdFaculty = facultyService.createFaculty(faculty);

                if (createdFaculty != faculty.getId()) {
                    redirectAttributes.addFlashAttribute(Message.ERROR, Message.FAILURE);
                } else {
                    redirectAttributes.addFlashAttribute(Message.SUCCESS, Message.CREATE_SUCCESS);
                }
            } catch (IllegalStateException ex) {
                redirectAttributes.addFlashAttribute(Message.ERROR, ex.getLocalizedMessage());
            }
        }
        return "redirect:/faculty/create-faculty";
    }

    @RolesAllowed({ "ADMIN", "STAFF" })
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

    @RolesAllowed("ADMIN")
    @PostMapping("/faculty/edit-faculty/{facultyId}")
    public String updateFaculty(@PathVariable("facultyId") int facultyId,
            @ModelAttribute("faculty") @Validated Faculty updatedFaculty, BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingValidator.validate(bindingResult, redirectAttributes)) {
            try {
                Faculty resultFaculty = facultyService.updateFacultyById(facultyId, updatedFaculty);

                if (resultFaculty != null) {
                    redirectAttributes.addFlashAttribute(Message.SUCCESS, Message.UPDATE_SUCCESS);
                } else {
                    redirectAttributes.addFlashAttribute(Message.ERROR, Message.FAILURE);
                }
            } catch (NoSuchElementException | IllegalStateException ex) {
                redirectAttributes.addFlashAttribute(Message.ERROR, ex.getLocalizedMessage());
            }
        }
        return "redirect:/faculty/faculty-card/" + facultyId;
    }

}
