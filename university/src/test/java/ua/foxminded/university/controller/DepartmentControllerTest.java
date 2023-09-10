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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import ua.foxminded.university.dao.entities.Admin;
import ua.foxminded.university.dao.entities.Department;
import ua.foxminded.university.dao.entities.Faculty;
import ua.foxminded.university.dao.service.AlertService;
import ua.foxminded.university.dao.service.DepartmentService;
import ua.foxminded.university.dao.service.FacultyService;
import ua.foxminded.university.security.UserDetailsServiceImpl;
import ua.foxminded.university.security.UserRole;
import ua.foxminded.university.validation.ControllerBindingValidator;
import ua.foxminded.university.validation.IdCollector;
import ua.foxminded.university.validation.Message;

@WebMvcTest({ DepartmentController.class, ControllerBindingValidator.class, IdCollector.class })
@ActiveProfiles("test-container")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class DepartmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DepartmentService departmentService;

    @MockBean
    private FacultyService facultyService;

    @MockBean
    private AlertService alertService;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @Test
    @WithMockUser(roles = { "ADMIN", "TEACHER", "STAFF" })
    void testSendDepartmentAlert() throws Exception {
        int departmentId = 1;
        String alertMessage = "Test Alert Message";

        Admin admin = new Admin();
        admin.setEmail("admin@example.ua");

        Authentication auth = new UsernamePasswordAuthenticationToken(admin.getEmail(), null,
                AuthorityUtils.createAuthorityList("ROLE_" + UserRole.ADMIN));
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(userDetailsService.getUserByUsername(admin.getEmail())).thenReturn(admin);

        mockMvc.perform(MockMvcRequestBuilders.post("/department/send-alert/{departmentId}", departmentId)
                .param("alertMessage", alertMessage).with(csrf().asHeader()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.flash().attributeExists(Message.SUCCESS))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/department/department-card/" + departmentId));
    }

    @Test
    @WithMockUser(roles = { "ADMIN", "STUDENT", "TEACHER", "STAFF" })
    void testGetAllDepartmentList() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/department/department-list"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("departments"))
                .andExpect(MockMvcResultMatchers.view().name("department/department-list"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteDepartment() throws Exception {
        int departmentId = 1;
        mockMvc.perform(
                MockMvcRequestBuilders.post("/department/delete/{departmentId}", departmentId).with(csrf().asHeader()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.flash().attributeExists(Message.SUCCESS))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/department/department-list"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testShowCreateDepartmentForm() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/department/create-department"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("department/create-department"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateDepartment() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/department/create-department").with(csrf().asHeader()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.flash().attributeCount(1))
                .andExpect(MockMvcResultMatchers.flash().attributeExists(Message.SUCCESS));
    }

    @Test
    @WithMockUser(roles = { "ADMIN", "STUDENT", "TEACHER", "STAFF" })
    void testSearchDepartment_WhenSearchTypeIsDepartment() throws Exception {
        Faculty faculty = new Faculty("Faculty A");
        Department department = new Department("Department A", faculty);
        department.setId(1);

        when(departmentService.findDepartmentById(department.getId())).thenReturn(Optional.of(department));

        mockMvc.perform(MockMvcRequestBuilders.get("/department/search-result").param("searchType", "department")
                .param("departmentId", String.valueOf(department.getId())))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("department/department-list"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("departments"))
                .andExpect(MockMvcResultMatchers.model().attribute("departments", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.model().attribute("departments", Matchers.contains(department)));
    }

    @Test
    @WithMockUser(roles = { "ADMIN", "STUDENT", "TEACHER", "STAFF" })
    void testSearchDepartment_WhenSearchTypeIsName() throws Exception {
        Faculty faculty = new Faculty("Faculty A");
        Department department = new Department("Department A", faculty);
        department.setId(1);

        when(departmentService.findDepartmentByName(department.getName()))
                .thenReturn(Collections.singletonList(department));

        mockMvc.perform(MockMvcRequestBuilders.get("/department/search-result").param("searchType", "name")
                .param("name", department.getName())).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("department/department-list"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("departments"))
                .andExpect(MockMvcResultMatchers.model().attribute("departments", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.model().attribute("departments", Matchers.contains(department)));
    }

    @Test
    @WithMockUser(roles = { "ADMIN", "STUDENT", "TEACHER", "STAFF" })
    void testSearchDepartment_WhenSearchTypeIsFaculty() throws Exception {
        Faculty faculty = new Faculty("Faculty A");
        Department department1 = new Department("Department A", faculty);
        department1.setId(1);
        Department department2 = new Department("Department B", faculty);
        department2.setId(2);
        List<Department> departments = Arrays.asList(department1, department2);

        when(departmentService.findAllByFacultyName(faculty.getFacultyName())).thenReturn(departments);

        mockMvc.perform(MockMvcRequestBuilders.get("/department/search-result").param("searchType", "faculty")
                .param("facultyName", faculty.getFacultyName())).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("department/department-list"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("departments"))
                .andExpect(MockMvcResultMatchers.model().attribute("departments", Matchers.hasSize(2)))
                .andExpect(MockMvcResultMatchers.model().attribute("departments",
                        Matchers.contains(department1, department2)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testOpenDepartmentCard_WhenDepartmentExists() throws Exception {
        Faculty faculty = new Faculty("Faculty A");
        Department department = new Department("Department A", faculty);
        department.setId(1);

        when(departmentService.findDepartmentById(department.getId())).thenReturn(Optional.of(department));

        mockMvc.perform(MockMvcRequestBuilders.get("/department/department-card/{departmentId}", department.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("department/department-card"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("department"))
                .andExpect(MockMvcResultMatchers.model().attribute("department", Matchers.sameInstance(department)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testOpenDepartmentCard_WhenDepartmentDoesNotExist() throws Exception {
        int departmentId = 999;

        when(departmentService.findDepartmentById(departmentId)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/department/department-card/{departmentId}", departmentId))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/department/department-list"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateDepartment_Success() throws Exception {
        int departmentId = 1;
        Department updatedDepartment = new Department();
        updatedDepartment.setId(departmentId);

        when(departmentService.updateDepartmentById(departmentId, updatedDepartment)).thenReturn(updatedDepartment);

        mockMvc.perform(MockMvcRequestBuilders.post("/department/edit-department/{departmentId}", departmentId)
                .flashAttr("department", updatedDepartment).with(csrf().asHeader()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.flash().attributeExists(Message.SUCCESS))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/department/department-card/" + departmentId));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateDepartment_Failure() throws Exception {
        int departmentId = 1;
        Department updatedDepartment = new Department();
        updatedDepartment.setId(departmentId);

        when(departmentService.updateDepartmentById(departmentId, updatedDepartment)).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.post("/department/edit-department/{departmentId}", departmentId)
                .flashAttr("department", updatedDepartment).with(csrf().asHeader()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.flash().attributeExists(Message.ERROR))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/department/department-card/" + departmentId));
    }
}
