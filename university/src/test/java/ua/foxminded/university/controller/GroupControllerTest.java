package ua.foxminded.university.controller;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ua.foxminded.university.dao.entities.Faculty;
import ua.foxminded.university.dao.entities.Group;
import ua.foxminded.university.dao.service.AlertService;
import ua.foxminded.university.dao.service.FacultyService;
import ua.foxminded.university.dao.service.GroupService;
import ua.foxminded.university.security.UserAuthenticationService;
import ua.foxminded.university.validation.ControllerBindingValidator;
import ua.foxminded.university.validation.IdCollector;
import ua.foxminded.university.validation.Message;

@WebMvcTest({ GroupController.class, ControllerBindingValidator.class, IdCollector.class })
@ActiveProfiles("test-container")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class GroupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GroupService groupService;

    @MockBean
    private FacultyService facultyService;

    @MockBean
    private AlertService alertService;

    @MockBean
    private UserAuthenticationService authenticationService;

    @Test
    @WithMockUser(roles = { "ADMIN", "STUDENT" })
    void testSendGroupAlert() throws Exception {
        int groupId = 1;
        String alertMessage = "Test Alert Message";

        mockMvc.perform(MockMvcRequestBuilders.post("/group/send-alert/{groupId}", groupId)
                .param("alertMessage", alertMessage).with(csrf().asHeader()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.flash().attributeExists(Message.SUCCESS))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/group/group-card/" + groupId));
    }

    @Test
    @WithMockUser(roles = { "ADMIN", "STUDENT", "TEACHER", "STAFF" })
    void testGetAllGroupList() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/group/group-list"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("groups"))
                .andExpect(MockMvcResultMatchers.view().name("group/group-list"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteGroup() throws Exception {
        int groupId = 1;
        mockMvc.perform(MockMvcRequestBuilders.post("/group/delete/{grouptId}", groupId).with(csrf().asHeader()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.flash().attributeExists(Message.SUCCESS))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/group/group-list"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testShowCreateGroupForm() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/group/create-group"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("group/create-group"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateGroup() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/group/create-group").with(csrf().asHeader()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.flash().attributeCount(1))
                .andExpect(MockMvcResultMatchers.flash().attributeExists(Message.SUCCESS));
    }

    @Test
    @WithMockUser(roles = { "ADMIN", "STUDENT", "TEACHER", "STAFF" })
    void testSearchGroup_WhenSearchTypeIsGroup() throws Exception {
        Faculty faculty = new Faculty("Faculty A");
        Group group = new Group("Group A", faculty);
        group.setId(1);

        when(groupService.findGroupById(group.getId())).thenReturn(Optional.of(group));

        mockMvc.perform(MockMvcRequestBuilders.get("/group/search-result").param("searchType", "group").param("groupId",
                String.valueOf(group.getId()))).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("group/group-list"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("groups"))
                .andExpect(MockMvcResultMatchers.model().attribute("groups", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.model().attribute("groups", Matchers.contains(group)));
    }

    @Test
    @WithMockUser(roles = { "ADMIN", "STUDENT", "TEACHER", "STAFF" })
    void testSearchGroup_WhenSearchTypeIsGroupName() throws Exception {
        Faculty faculty = new Faculty("Faculty A");
        Group group = new Group("Group A", faculty);
        group.setId(1);

        when(groupService.findGroupByGroupName(group.getGroupName())).thenReturn(Collections.singletonList(group));

        mockMvc.perform(MockMvcRequestBuilders.get("/group/search-result").param("searchType", "groupName")
                .param("groupName", group.getGroupName())).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("group/group-list"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("groups"))
                .andExpect(MockMvcResultMatchers.model().attribute("groups", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.model().attribute("groups", Matchers.contains(group)));
    }

    @Test
    @WithMockUser(roles = { "ADMIN", "STUDENT", "TEACHER", "STAFF" })
    void testSearchGroup_WhenSearchTypeIsFaculty() throws Exception {
        Faculty faculty = new Faculty("Faculty A");
        Group group1 = new Group("Group A", faculty);
        group1.setId(1);
        Group group2 = new Group("Group B", faculty);
        group2.setId(2);
        List<Group> groups = Arrays.asList(group1, group2);

        when(groupService.findAllByFacultyName(faculty.getFacultyName())).thenReturn(groups);

        mockMvc.perform(MockMvcRequestBuilders.get("/group/search-result").param("searchType", "faculty")
                .param("facultyName", faculty.getFacultyName())).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("group/group-list"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("groups"))
                .andExpect(MockMvcResultMatchers.model().attribute("groups", Matchers.hasSize(2)))
                .andExpect(MockMvcResultMatchers.model().attribute("groups", Matchers.contains(group1, group2)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testOpenGrouptCard_WhenGroupExists() throws Exception {
        Faculty faculty = new Faculty("Faculty A");
        Group group = new Group("Group A", faculty);
        group.setId(1);

        when(groupService.findGroupById(group.getId())).thenReturn(Optional.of(group));

        mockMvc.perform(MockMvcRequestBuilders.get("/group/group-card/{groupId}", group.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("group/group-card"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("group"))
                .andExpect(MockMvcResultMatchers.model().attribute("group", Matchers.sameInstance(group)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testOpenGroupCard_WhenGroupDoesNotExist() throws Exception {
        int groupId = 999;

        when(groupService.findGroupById(groupId)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/group/group-card/{groupId}", groupId))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/group/group-list"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateGroup_Success() throws Exception {
        int groupId = 1;
        Group updatedGroup = new Group();
        updatedGroup.setId(groupId);

        when(groupService.updateGroupById(groupId, updatedGroup)).thenReturn(updatedGroup);

        mockMvc.perform(MockMvcRequestBuilders.post("/group/edit-group/{groupId}", groupId)
                .flashAttr("group", updatedGroup).with(csrf().asHeader()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.flash().attributeExists(Message.SUCCESS))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/group/group-card/" + groupId));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateGroup_Failure() throws Exception {
        int groupId = 1;
        Group updatedGroup = new Group();
        updatedGroup.setId(groupId);

        when(groupService.updateGroupById(groupId, updatedGroup)).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.post("/group/edit-group/{groupId}", groupId)
                .flashAttr("group", updatedGroup).with(csrf().asHeader()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.flash().attributeExists(Message.ERROR))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/group/group-card/" + groupId));
    }
}
