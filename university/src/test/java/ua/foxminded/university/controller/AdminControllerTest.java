package ua.foxminded.university.controller;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
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
import ua.foxminded.university.dao.entities.Admin;
import ua.foxminded.university.dao.entities.Alert;
import ua.foxminded.university.dao.service.AdminService;
import ua.foxminded.university.dao.service.AlertService;
import ua.foxminded.university.security.UserAuthenticationService;
import ua.foxminded.university.validation.ControllerBindingValidator;
import ua.foxminded.university.validation.Message;

@WebMvcTest({ AdminController.class, ControllerBindingValidator.class })
@ActiveProfiles("test-container")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@WithMockUser(roles = "ADMIN")
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminService adminService;

    @MockBean
    private AlertService alertService;

    @MockBean
    private UserAuthenticationService authenticationService;

    @Test
    void testAdminDashboard_WhenUserAuthenticated() throws Exception {
        Admin admin = new Admin();

        when(adminService.findAdminByEmail(authenticationService.getAuthenticatedUsername()))
                .thenReturn(Optional.of(admin));

        mockMvc.perform(MockMvcRequestBuilders.get("/admin/main")).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("admin/main"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("admin"))
                .andExpect(MockMvcResultMatchers.model().attribute("admin", admin));
    }

    @Test
    void testAdminDashboard_WhenUserNotAuthenticated() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/main"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/login"));
    }

    @Test
    void testUpdatePersonalData_Success() throws Exception {
        int adminId = 1;
        Admin updatedAdmin = new Admin();

        when(adminService.updateAdminById(adminId, updatedAdmin)).thenReturn(updatedAdmin);

        mockMvc.perform(MockMvcRequestBuilders.post("/admin/update-personal/{adminId}", adminId).with(csrf().asHeader())
                .flashAttr("admin", updatedAdmin)).andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.flash().attributeExists(Message.SUCCESS))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/admin/main"));
    }

    @Test
    void testUpdatePersonalData_Failure() throws Exception {
        int adminId = 1;
        mockMvc.perform(
                MockMvcRequestBuilders.post("/admin/update-personal/{adminId}", adminId).with(csrf().asHeader()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.flash().attributeExists(Message.ERROR))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/admin/main"));
    }

    @ParameterizedTest
    @CsvSource({ "1, password, newPassword" })
    void testUpdatePassword_Success(int adminId, String oldPassword, String newPassword) throws Exception {
        Admin resultAdmin = new Admin();
        resultAdmin.setId(1);
        resultAdmin.setHashedPassword(newPassword);

        when(adminService.changeAdminPasswordById(adminId, oldPassword, newPassword)).thenReturn(resultAdmin);

        mockMvc.perform(MockMvcRequestBuilders.post("/admin/update-password").param("adminId", String.valueOf(adminId))
                .param("oldPassword", oldPassword).param("newPassword", newPassword).with(csrf().asHeader()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.flash().attributeExists(Message.SUCCESS))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/admin/main"));
    }

    @ParameterizedTest
	@CsvSource({ "-1, password, newPassword" })
	void testUpdatePassword_Failure_NoSuchElementException(int adminId, String oldPassword, String newPassword)
			throws Exception {
		when(adminService.changeAdminPasswordById(adminId, oldPassword, newPassword))
				.thenReturn(null);

		mockMvc.perform(MockMvcRequestBuilders.post("/admin/update-password").param("adminId", String.valueOf(adminId))
				.param("oldPassword", oldPassword).param("newPassword", newPassword).with(csrf().asHeader()))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.flash().attributeExists(Message.ERROR))
				.andExpect(MockMvcResultMatchers.redirectedUrl("/admin/main"));
	}

    @ParameterizedTest
	@CsvSource({ "1, wrongOldpassword, newPassword" })
	void testUpdatePassword_Failure_WrongOldPassword(int adminId, String wrongOldPassword, String newPassword)
			throws Exception {
		when(adminService.changeAdminPasswordById(adminId, wrongOldPassword, newPassword))
				.thenThrow(IllegalStateException.class);

		mockMvc.perform(MockMvcRequestBuilders.post("/admin/update-password").param("adminId", String.valueOf(adminId))
				.param("oldPassword", wrongOldPassword).param("newPassword", newPassword).with(csrf().asHeader()))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.redirectedUrl("/admin/main"));
	}

    @Test
    void testUpdateAdmin_Success() throws Exception {
        int adminId = 1;
        Admin updatedAdmin = new Admin();
        updatedAdmin.setId(adminId);

        when(adminService.updateAdminById(adminId, updatedAdmin)).thenReturn(updatedAdmin);

        mockMvc.perform(MockMvcRequestBuilders.post("/admin/edit-admin/{adminId}", adminId)
                .flashAttr("admin", updatedAdmin).with(csrf().asHeader()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.flash().attributeExists(Message.SUCCESS))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/admin/admin-card/" + adminId));
    }

    @Test
    void testUpdateAdmin_Failure() throws Exception {
        int adminId = 1;
        Admin updatedAdmin = new Admin();
        updatedAdmin.setId(adminId);

        when(adminService.updateAdminById(adminId, updatedAdmin)).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.post("/admin/edit-admin/{adminId}", adminId)
                .flashAttr("admin", updatedAdmin).with(csrf().asHeader()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.flash().attributeExists(Message.ERROR))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/admin/admin-card/" + adminId));
    }

    @Test
    void testOpenAdminAlerts() throws Exception {
        Admin admin = new Admin();

        when(adminService.findAdminByEmail(authenticationService.getAuthenticatedUsername()))
                .thenReturn(Optional.of(admin));

        mockMvc.perform(MockMvcRequestBuilders.get("/admin/alert")).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("alert"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("admin"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("alerts"))
                .andExpect(MockMvcResultMatchers.model().attribute("admin", admin));
    }

    @Test
    void testSendAdminAlert() throws Exception {
        int adminId = 1;
        String alertMessage = "Test Alert Message";

        mockMvc.perform(MockMvcRequestBuilders.post("/admin/send-alert/{adminId}", adminId)
                .param("alertMessage", alertMessage).with(csrf().asHeader()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.flash().attributeExists(Message.SUCCESS))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/admin/admin-card/" + adminId));
    }

    @Test
    void testSendBroadcastAlert() throws Exception {
        String alertMessage = "Test Alert Message";

        mockMvc.perform(MockMvcRequestBuilders.post("/admin/send-broadcast").param("alertMessage", alertMessage)
                .with(csrf().asHeader())).andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.flash().attributeExists(Message.SUCCESS))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/admin/alert"));
    }

    @Test
    void testDeleteAdminAlert() throws Exception {
        int alertId = 1;
        mockMvc.perform(MockMvcRequestBuilders.post("/admin/remove-alert/{alertId}", alertId).with(csrf().asHeader()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.flash().attributeExists(Message.SUCCESS))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/admin/alert"));
    }

    @Test
    void testToggleAdminAlert() throws Exception {
        int alertId = 1;
        mockMvc.perform(
                MockMvcRequestBuilders.post("/admin/mark-alert-as-read/{alertId}", alertId).with(csrf().asHeader()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/admin/alert"));
    }

    @ParameterizedTest
    @CsvSource({ "2023-09-01, 2023-10-01, TestSender, Test Message" })
    void testGetSelectedDateAdminAlerts(LocalDate dateFrom, LocalDate dateTo, String sender, String message)
            throws Exception {
        Admin admin = new Admin();
        admin.setId(1);

        LocalDateTime from = dateFrom.atStartOfDay();
        LocalDateTime to = dateTo.atTime(LocalTime.MAX);

        Alert alert1 = new Alert(from, sender, admin, message);
        Alert alert2 = new Alert(to, sender, admin, message);
        List<Alert> alerts = Arrays.asList(alert1, alert2);

        when(adminService.findAdminById(admin.getId())).thenReturn(Optional.of(admin));
        when(alertService.findByAdminAndDateBetween(admin.getId(), from, to)).thenReturn(alerts);

        mockMvc.perform(MockMvcRequestBuilders.get("/admin/selected-alert/{adminId}", admin.getId())
                .param("dateFrom", String.valueOf(dateFrom)).param("dateTo", String.valueOf(dateTo)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("alerts"))
                .andExpect(MockMvcResultMatchers.view().name("alert"))
                .andExpect(MockMvcResultMatchers.model().attribute("alerts", Matchers.contains(alert1, alert2)));
    }

    @Test
    void testGetAllAdmin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/admin-list"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("admins"))
                .andExpect(MockMvcResultMatchers.view().name("admin/admin-list"));
    }

    @Test
    void testDeleteAdmin() throws Exception {
        int adminId = 1;
        mockMvc.perform(MockMvcRequestBuilders.post("/admin/delete/{adminId}", adminId).with(csrf().asHeader()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.flash().attributeExists(Message.SUCCESS))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/admin/admin-list"));
    }

    @Test
    void testOpenAdminCard_WhenAdminExists() throws Exception {
        Admin admin = new Admin();
        admin.setId(1);

        when(adminService.findAdminById(admin.getId())).thenReturn(Optional.of(admin));

        mockMvc.perform(MockMvcRequestBuilders.get("/admin/admin-card/{adminId}", admin.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("admin/admin-card"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("admin"))
                .andExpect(MockMvcResultMatchers.model().attribute("admin", Matchers.sameInstance(admin)));
    }

    @Test
    void testOpenAdminCard_WhenAdminDoesNotExist() throws Exception {
        int adminId = 999;

        when(adminService.findAdminById(adminId)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/admin/admin-card/{adminId}", adminId))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/admin/admin-list"));
    }

    @Test
    void testShowCreateAdminForm() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/create-admin"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("admin/create-admin"));
    }

    @Test
    void testCreateAdmin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/admin/create-admin").with(csrf().asHeader()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.flash().attributeCount(1))
                .andExpect(MockMvcResultMatchers.flash().attributeExists(Message.SUCCESS));
    }

}
