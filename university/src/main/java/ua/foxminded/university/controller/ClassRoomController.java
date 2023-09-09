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
import ua.foxminded.university.dao.entities.ClassRoom;
import ua.foxminded.university.dao.service.ClassRoomService;
import ua.foxminded.university.validation.ControllerBindingValidator;
import ua.foxminded.university.validation.IdValidator;
import ua.foxminded.university.validation.Message;

@Controller
public class ClassRoomController {

    @Autowired
    private ClassRoomService classRoomService;

    @Autowired
    private ControllerBindingValidator bindingValidator;

    @Autowired
    private IdValidator idValidator;

    @GetMapping("/classroom/classroom-list")
    public String getAllClassRoomList(Model model) {
        List<ClassRoom> classRooms = classRoomService.getAllClassRooms();

        for (ClassRoom classroom : classRooms) {
            classroom.getTimeTables();
        }

        model.addAttribute("classrooms", classRooms);
        return "classroom/classroom-list";
    }

    @RolesAllowed("ADMIN")
    @PostMapping("/classroom/delete/{classroomId}")
    public String deleteClassRoom(@PathVariable int classroomId, RedirectAttributes redirectAttributes,
            HttpServletRequest request) {
        try {
            classRoomService.deleteClassRoomById(classroomId);
            redirectAttributes.addFlashAttribute(Message.SUCCESS, Message.DELETE_SUCCESS);
        } catch (NoSuchElementException ex) {
            redirectAttributes.addFlashAttribute(Message.ERROR, ex.getLocalizedMessage());
        }
        String referrer = request.getHeader("referer");

        if (referrer == null || referrer.isEmpty()) {
            return "redirect:/classroom/classroom-list";
        }
        return "redirect:" + referrer;
    }

    @RolesAllowed("ADMIN")
    @GetMapping("/classroom/create-classroom")
    public String showCreateClassRoomForm() {
        return "classroom/create-classroom";
    }

    @RolesAllowed("ADMIN")
    @PostMapping("/classroom/create-classroom")
    public String createClassRoom(@ModelAttribute("course") @Validated ClassRoom classroom, BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingValidator.validate(bindingResult, redirectAttributes)) {
            try {
                int createdClassRoom = classRoomService.createClassRoom(classroom);

                if (createdClassRoom != classroom.getId()) {
                    redirectAttributes.addFlashAttribute(Message.ERROR, Message.FAILURE);
                } else {
                    redirectAttributes.addFlashAttribute(Message.SUCCESS, Message.CREATE_SUCCESS);
                }
            } catch (IllegalStateException ex) {
                redirectAttributes.addFlashAttribute(Message.ERROR, ex.getLocalizedMessage());
            }
        }
        return "redirect:/classroom/create-classroom";
    }

    @GetMapping("/classroom/search-result")
    public String searchClassRoom(@RequestParam("searchType") String searchType,
            @RequestParam(required = false) String classroomId, @RequestParam(required = false) String street,
            @RequestParam(required = false) String buildingNumber, @RequestParam(required = false) Integer roomNumber,
            Model model) {
        List<ClassRoom> classRoomList = new ArrayList<>();

        if ("classroom".equals(searchType)) {
            Optional<ClassRoom> optionalClassRoom = classRoomService
                    .findClassRoomById(idValidator.digitsCollector(classroomId));
            classRoomList = optionalClassRoom.map(Collections::singletonList).orElse(Collections.emptyList());
        } else if ("street".equals(searchType)) {
            classRoomList = classRoomService.findClassRoomsByStreet(street);
        } else if ("streetAndBuildingNumber".equals(searchType)) {
            classRoomList = classRoomService.findClassRoomsByStreetAndBuildingNumber(street,
                    idValidator.digitsCollector(buildingNumber));
        }
        model.addAttribute("classrooms", classRoomList);
        return "classroom/classroom-list";
    }

    @RolesAllowed("ADMIN")
    @GetMapping("/classroom/classroom-card/{classroomId}")
    public String openClassRoomCard(@PathVariable int classroomId, Model model) {
        Optional<ClassRoom> optionalClassRoom = classRoomService.findClassRoomById(classroomId);

        if (optionalClassRoom.isPresent()) {
            ClassRoom classRoom = optionalClassRoom.get();
            model.addAttribute("classroom", classRoom);
            return "classroom/classroom-card";
        } else {
            return "redirect:/classroom/classroom-list";
        }
    }

    @RolesAllowed("ADMIN")
    @PostMapping("/classroom/edit-classroom/{classroomId}")
    public String updateClassRoom(@PathVariable("classroomId") int classroomId,
            @ModelAttribute("classroom") @Validated ClassRoom updatedClassRoom, BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingValidator.validate(bindingResult, redirectAttributes)) {
            try {
                ClassRoom resultClassRoom = classRoomService.updateClassRoomById(classroomId, updatedClassRoom);

                if (resultClassRoom != null) {
                    redirectAttributes.addFlashAttribute(Message.SUCCESS, Message.UPDATE_SUCCESS);
                } else {
                    redirectAttributes.addFlashAttribute(Message.ERROR, Message.FAILURE);
                }
            } catch (NoSuchElementException | IllegalStateException ex) {
                redirectAttributes.addFlashAttribute(Message.ERROR, ex.getLocalizedMessage());
            }
        }
        return "redirect:/classroom/classroom-card/" + classroomId;
    }
}
