package ua.foxminded.university.controller;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ua.foxminded.university.dao.entities.Alert;
import ua.foxminded.university.dao.entities.Staff;
import ua.foxminded.university.dao.service.AlertService;
import ua.foxminded.university.dao.service.StaffService;
import ua.foxminded.university.security.UserAuthenticationService;
import ua.foxminded.university.validation.ControllerBindingValidator;
import ua.foxminded.university.validation.IdCollector;
import ua.foxminded.university.validation.Message;

@WebMvcTest({ StaffController.class, ControllerBindingValidator.class, IdCollector.class })
@ActiveProfiles("test-container")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class StaffControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StaffService staffService;

    @MockBean
    private AlertService alertService;

    @MockBean
    private UserAuthenticationService authenticationService;

    @Test
    @WithMockUser(roles = "STAFF")
    void testStaffDashboard_WhenUserAuthenticated() throws Exception {
        Staff staff = new Staff();

        when(staffService.findStaffByEmail(authenticationService.getAuthenticatedUsername()))
                .thenReturn(Optional.of(staff));

        mockMvc.perform(MockMvcRequestBuilders.get("/staff/main")).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("staff/main"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("staff"))
                .andExpect(MockMvcResultMatchers.model().attribute("staff", staff));
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void testStaffDashboard_WhenUserNotAuthenticated() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/staff/main"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/login"));
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void testUpdatePersonalData_Success() throws Exception {
        int staffId = 1;
        Staff updatedStaff = new Staff();

        when(staffService.updateStaffById(staffId, updatedStaff)).thenReturn(updatedStaff);

        mockMvc.perform(MockMvcRequestBuilders.post("/staff/update-personal/{staffId}", staffId).with(csrf().asHeader())
                .flashAttr("staff", updatedStaff)).andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.flash().attributeExists(Message.SUCCESS))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/staff/main"));
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void testUpdatePersonalData_Failure() throws Exception {
        int staffId = 1;
        mockMvc.perform(
                MockMvcRequestBuilders.post("/staff/update-personal/{staffId}", staffId).with(csrf().asHeader()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.flash().attributeExists(Message.ERROR))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/staff/main"));
    }

    @ParameterizedTest
    @CsvSource({ "1, password, newPassword" })
    @WithMockUser(roles = "STAFF")
    void testUpdatePassword_Success(int staffId, String oldPassword, String newPassword) throws Exception {
        Staff resultStaff = new Staff();
        resultStaff.setId(1);
        resultStaff.setHashedPassword(newPassword);

        when(staffService.changeStaffPasswordById(staffId, oldPassword, newPassword)).thenReturn(resultStaff);

        mockMvc.perform(MockMvcRequestBuilders.post("/staff/update-password").param("staffId", String.valueOf(staffId))
                .param("oldPassword", oldPassword).param("newPassword", newPassword).with(csrf().asHeader()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.flash().attributeExists(Message.SUCCESS))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/staff/main"));
    }

    @ParameterizedTest
	@CsvSource({ "-1, password, newPassword" })
	@WithMockUser(roles = "STAFF")
	void testUpdatePassword_Failure_NoSuchElementException(int staffId, String oldPassword, String newPassword)
			throws Exception {
		when(staffService.changeStaffPasswordById(staffId, oldPassword, newPassword))
				.thenReturn(null);

		mockMvc.perform(MockMvcRequestBuilders.post("/staff/update-password").param("staffId", String.valueOf(staffId))
				.param("oldPassword", oldPassword).param("newPassword", newPassword).with(csrf().asHeader()))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.flash().attributeExists(Message.ERROR))
				.andExpect(MockMvcResultMatchers.redirectedUrl("/staff/main"));
	}

    @ParameterizedTest
	@CsvSource({ "1, wrongOldpassword, newPassword" })
	@WithMockUser(roles = "STAFF")
	void testUpdatePassword_Failure_WrongOldPassword(int staffId, String wrongOldPassword, String newPassword)
			throws Exception {
		when(staffService.changeStaffPasswordById(staffId, wrongOldPassword, newPassword))
				.thenThrow(IllegalStateException.class);

		mockMvc.perform(MockMvcRequestBuilders.post("/staff/update-password").param("staffId", String.valueOf(staffId))
				.param("oldPassword", wrongOldPassword).param("newPassword", newPassword).with(csrf().asHeader()))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.redirectedUrl("/staff/main"));
	}

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateStaff_Success() throws Exception {
        int staffId = 1;
        Staff updatedStaff = new Staff();
        updatedStaff.setId(staffId);

        when(staffService.updateStaffById(staffId, updatedStaff)).thenReturn(updatedStaff);

        mockMvc.perform(MockMvcRequestBuilders.post("/staff/edit-staff/{staffId}", staffId)
                .flashAttr("staff", updatedStaff).with(csrf().asHeader()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.flash().attributeExists(Message.SUCCESS))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/staff/staff-card/" + staffId));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateStaff_Failure() throws Exception {
        int staffId = 1;
        Staff updatedStaff = new Staff();
        updatedStaff.setId(staffId);

        when(staffService.updateStaffById(staffId, updatedStaff)).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.post("/staff/edit-staff/{staffId}", staffId)
                .flashAttr("staff", updatedStaff).with(csrf().asHeader()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.flash().attributeExists(Message.ERROR))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/staff/staff-card/" + staffId));
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void testOpenStaffAlerts() throws Exception {
        Staff staff = new Staff();

        when(staffService.findStaffByEmail(authenticationService.getAuthenticatedUsername()))
                .thenReturn(Optional.of(staff));

        mockMvc.perform(MockMvcRequestBuilders.get("/staff/alert")).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("alert"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("staff"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("alerts"))
                .andExpect(MockMvcResultMatchers.model().attribute("staff", staff));
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void testSendStaffAlert() throws Exception {
        int staffId = 1;
        String alertMessage = "Test Alert Message";

        mockMvc.perform(MockMvcRequestBuilders.post("/staff/send-alert/{staffId}", staffId)
                .param("alertMessage", alertMessage).with(csrf().asHeader()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.flash().attributeExists(Message.SUCCESS))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/staff/staff-card/" + staffId));
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void testSendBroadcastAlert() throws Exception {
        String alertMessage = "Test Alert Message";

        mockMvc.perform(MockMvcRequestBuilders.post("/staff/send-broadcast").param("alertMessage", alertMessage)
                .with(csrf().asHeader())).andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.flash().attributeExists(Message.SUCCESS))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/staff/alert"));
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void testDeleteStaffAlert() throws Exception {
        int alertId = 1;
        mockMvc.perform(MockMvcRequestBuilders.post("/staff/remove-alert/{alertId}", alertId).with(csrf().asHeader()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.flash().attributeExists(Message.SUCCESS))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/staff/alert"));
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void testToggleStaffAlert() throws Exception {
        int alertId = 1;
        mockMvc.perform(
                MockMvcRequestBuilders.post("/staff/mark-alert-as-read/{alertId}", alertId).with(csrf().asHeader()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/staff/alert"));
    }

    @ParameterizedTest
    @CsvSource({ "2023-09-01, 2023-10-01, TestSender, Test Message" })
    @WithMockUser(roles = "STAFF")
    void testGetSelectedDateStaffAlerts(LocalDate dateFrom, LocalDate dateTo, String sender, String message)
            throws Exception {
        Staff staff = new Staff();
        staff.setId(1);

        LocalDateTime from = dateFrom.atStartOfDay();
        LocalDateTime to = dateTo.atTime(LocalTime.MAX);

        Alert alert1 = new Alert(from, sender, staff, message);
        Alert alert2 = new Alert(to, sender, staff, message);
        List<Alert> alerts = Arrays.asList(alert1, alert2);

        when(staffService.findStaffById(staff.getId())).thenReturn(Optional.of(staff));
        when(alertService.findByStaffAndDateBetween(staff.getId(), from, to)).thenReturn(alerts);

        mockMvc.perform(MockMvcRequestBuilders.get("/staff/selected-alert/{staffId}", staff.getId())
                .param("dateFrom", String.valueOf(dateFrom)).param("dateTo", String.valueOf(dateTo)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("alerts"))
                .andExpect(MockMvcResultMatchers.view().name("alert"))
                .andExpect(MockMvcResultMatchers.model().attribute("alerts", Matchers.contains(alert1, alert2)));
    }

    @Test
    @WithMockUser(roles = { "ADMIN", "STUDENT", "TEACHER", "STAFF" })
    void testGetAllStaffList() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/staff/staff-list"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("staff"))
                .andExpect(MockMvcResultMatchers.view().name("staff/staff-list"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteStaff() throws Exception {
        int staffId = 1;
        mockMvc.perform(MockMvcRequestBuilders.post("/staff/delete/{staffId}", staffId).with(csrf().asHeader()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.flash().attributeExists(Message.SUCCESS))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/staff/staff-list"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testShowCreateStaffForm() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/staff/create-staff"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("staff/create-staff"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateStaff() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/staff/create-staff").with(csrf().asHeader()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.flash().attributeCount(1))
                .andExpect(MockMvcResultMatchers.flash().attributeExists(Message.SUCCESS));
    }

    @Test
    @WithMockUser(roles = { "ADMIN", "STUDENT", "TEACHER", "STAFF" })
    void testSearchStaff_WhenSearchTypeIsStaff() throws Exception {
        Staff staff = new Staff("Argus", "Filtch", true, "argus@mail.com", "1234", "Techical manager", null);
        staff.setId(1);

        when(staffService.findStaffById(staff.getId())).thenReturn(Optional.of(staff));

        mockMvc.perform(MockMvcRequestBuilders.get("/staff/search-result").param("searchType", "staff").param("staffId",
                String.valueOf(staff.getId()))).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("staff/staff-list"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("staff"))
                .andExpect(MockMvcResultMatchers.model().attribute("staff", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.model().attribute("staff", Matchers.contains(staff)));
    }

    @Test
    @WithMockUser(roles = { "ADMIN", "STUDENT", "TEACHER", "STAFF" })
    void testSearchStaff_WhenSearchTypeIsFirstNameAndLastName() throws Exception {
        Staff staff = new Staff("Argus", "Filtch", true, "argus@mail.com", "1234", "Techical manager", null);
        staff.setId(1);

        when(staffService.findStaffByName(staff.getFirstName(), staff.getLastName()))
                .thenReturn(Collections.singletonList(staff));

        mockMvc.perform(MockMvcRequestBuilders.get("/staff/search-result").param("searchType", "firstNameAndLastName")
                .param("firstName", staff.getFirstName()).param("lastName", staff.getLastName()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("staff/staff-list"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("staff"))
                .andExpect(MockMvcResultMatchers.model().attribute("staff", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.model().attribute("staff", Matchers.contains(staff)));
    }

    @Test
    @WithMockUser(roles = { "ADMIN", "STUDENT", "TEACHER", "STAFF" })
    void testSearchStaff_WhenSearchTypeIsPosition() throws Exception {
        Staff staff1 = new Staff("Argus", "Filtch", true, "argus@mail.com", "1234", "Techical manager", null);
        staff1.setId(1);
        Staff staff2 = new Staff("Rubeous", "Hagrid", true, "hagrid@mail.com", "1234", "Techical manager", null);
        staff2.setId(2);
        List<Staff> staff = Arrays.asList(staff1, staff2);

        when(staffService.findStaffByPosition(staff1.getPosition())).thenReturn(staff);

        mockMvc.perform(MockMvcRequestBuilders.get("/staff/search-result").param("searchType", "position")
                .param("position", staff1.getPosition())).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("staff/staff-list"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("staff"))
                .andExpect(MockMvcResultMatchers.model().attribute("staff", Matchers.hasSize(2)))
                .andExpect(MockMvcResultMatchers.model().attribute("staff", Matchers.contains(staff1, staff2)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testOpenStaffCard_WhenStaffExists() throws Exception {
        Staff staff = new Staff("Argus", "Filtch", true, "argus@mail.com", "1234", "Techical manager", null);
        staff.setId(1);

        when(staffService.findStaffById(staff.getId())).thenReturn(Optional.of(staff));

        mockMvc.perform(MockMvcRequestBuilders.get("/staff/staff-card/{staffId}", staff.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("staff/staff-card"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("staff"))
                .andExpect(MockMvcResultMatchers.model().attribute("staff", Matchers.sameInstance(staff)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testOpenStaffCard_WhenStaffDoesNotExist() throws Exception {
        int staffId = 999;

        when(staffService.findStaffById(staffId)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/staff/staff-card/{staffId}", staffId))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/staff/staff-list"));
    }
}
