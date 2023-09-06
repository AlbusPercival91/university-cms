package ua.foxminded.university.controller;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ua.foxminded.university.dao.entities.Course;
import ua.foxminded.university.dao.entities.Faculty;
import ua.foxminded.university.dao.entities.Group;
import ua.foxminded.university.dao.entities.Student;
import ua.foxminded.university.dao.service.AlertService;
import ua.foxminded.university.dao.service.CourseService;
import ua.foxminded.university.dao.service.GroupService;
import ua.foxminded.university.dao.service.StudentService;
import ua.foxminded.university.security.UserRole;
import ua.foxminded.university.validation.ControllerBindingValidator;
import ua.foxminded.university.validation.Message;

@WebMvcTest({ StudentController.class, ControllerBindingValidator.class })
@ActiveProfiles("test-container")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentService;

    @MockBean
    private CourseService courseService;

    @MockBean
    private GroupService groupService;

    @MockBean
    private AlertService alertService;

    @Test
    void testStudentDashboard_WhenUserAuthenticated() throws Exception {
        Faculty facultyGrifindor = new Faculty("Grifindor");
        Group group = new Group("Group A", facultyGrifindor);
        Student student = new Student("Harry", "Potter", true, "potter@mail.com", "1234", group);

        when(studentService.findStudentByEmail(student.getEmail())).thenReturn(Optional.of(student));

        Authentication auth = new UsernamePasswordAuthenticationToken(student.getEmail(), null,
                AuthorityUtils.createAuthorityList("ROLE_" + UserRole.STUDENT));
        SecurityContextHolder.getContext().setAuthentication(auth);

        mockMvc.perform(MockMvcRequestBuilders.get("/student/main")).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("student/main"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("student"))
                .andExpect(MockMvcResultMatchers.model().attribute("student", student));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void testStudentDashboard_WhenUserNotAuthenticated() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/student/main"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/login"));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void testUpdatePersonalData_Success() throws Exception {
        int studentId = 1;
        Student updatedStudent = new Student();

        when(studentService.updateStudentById(studentId, updatedStudent)).thenReturn(updatedStudent);

        mockMvc.perform(MockMvcRequestBuilders.post("/student/update-personal/{studentId}", studentId)
                .with(csrf().asHeader()).flashAttr("student", updatedStudent))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.flash().attributeExists(Message.SUCCESS))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/student/main"));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void testUpdatePersonalData_Failure() throws Exception {
        int studentId = 1;
        mockMvc.perform(
                MockMvcRequestBuilders.post("/student/update-personal/{studentId}", studentId).with(csrf().asHeader()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.flash().attributeExists(Message.ERROR))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/student/main"));
    }

    @ParameterizedTest
    @CsvSource({ "1, password, newPassword" })
    @WithMockUser(roles = "STUDENT")
    void testUpdatePassword_Success(int studentId, String oldPassword, String newPassword) throws Exception {
        Student resultStudent = new Student();
        resultStudent.setId(1);
        resultStudent.setHashedPassword(newPassword);

        when(studentService.changeStudentPasswordById(studentId, oldPassword, newPassword)).thenReturn(resultStudent);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/student/update-password").param("studentId", String.valueOf(studentId))
                        .param("oldPassword", oldPassword).param("newPassword", newPassword).with(csrf().asHeader()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.flash().attributeExists(Message.SUCCESS))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/student/main"));
    }

    @ParameterizedTest
	@CsvSource({ "-1, password, newPassword" })
	@WithMockUser(roles = "STUDENT")
	void testUpdatePassword_Failure_NoSuchElementException(int studentId, String oldPassword, String newPassword)
			throws Exception {
		when(studentService.changeStudentPasswordById(studentId, oldPassword, newPassword))
				.thenReturn(null);

		mockMvc.perform(MockMvcRequestBuilders.post("/student/update-password").param("studentId", String.valueOf(studentId))
				.param("oldPassword", oldPassword).param("newPassword", newPassword).with(csrf().asHeader()))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.flash().attributeExists(Message.ERROR))
				.andExpect(MockMvcResultMatchers.redirectedUrl("/student/main"));
	}

    @ParameterizedTest
	@CsvSource({ "1, wrongOldpassword, newPassword" })
	@WithMockUser(roles = "STUDENT")
	void testUpdatePassword_Failure_WrongOldPassword(int staffId, String wrongOldPassword, String newPassword)
			throws Exception {
		when(studentService.changeStudentPasswordById(staffId, wrongOldPassword, newPassword))
				.thenThrow(IllegalStateException.class);

		mockMvc.perform(MockMvcRequestBuilders.post("/student/update-password").param("studentId", String.valueOf(staffId))
				.param("oldPassword", wrongOldPassword).param("newPassword", newPassword).with(csrf().asHeader()))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.redirectedUrl("/student/main"));
	}

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateStudent_Success() throws Exception {
        int studentId = 1;
        Student updatedStudent = new Student();
        updatedStudent.setId(studentId);

        when(studentService.updateStudentById(studentId, updatedStudent)).thenReturn(updatedStudent);

        mockMvc.perform(MockMvcRequestBuilders.post("/student/edit-student/{studentId}", studentId)
                .flashAttr("student", updatedStudent).with(csrf().asHeader()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.flash().attributeExists(Message.SUCCESS))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/student/student-card/" + studentId));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateStudent_Failure() throws Exception {
        int studentId = 1;
        Student updatedStudent = new Student();
        updatedStudent.setId(studentId);

        when(studentService.updateStudentById(studentId, updatedStudent)).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.post("/student/edit-student/{studentId}", studentId)
                .flashAttr("student", updatedStudent).with(csrf().asHeader()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.flash().attributeExists(Message.ERROR))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/student/student-card/" + studentId));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void testOpenStudentAlerts() throws Exception {
        Student student = new Student();
        student.setEmail("student@example.ua");

        when(studentService.findStudentByEmail(student.getEmail())).thenReturn(Optional.of(student));

        Authentication auth = new UsernamePasswordAuthenticationToken(student.getEmail(), null,
                AuthorityUtils.createAuthorityList("ROLE_" + UserRole.STUDENT));
        SecurityContextHolder.getContext().setAuthentication(auth);

        mockMvc.perform(MockMvcRequestBuilders.get("/student/alert")).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("alert"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("student"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("alerts"))
                .andExpect(MockMvcResultMatchers.model().attribute("student", student));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void testSendStudentAlert() throws Exception {
        int studentId = 1;
        String alertMessage = "Test Alert Message";

        mockMvc.perform(MockMvcRequestBuilders.post("/student/send-alert/{studentId}", studentId)
                .param("alertMessage", alertMessage).with(csrf().asHeader()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.flash().attributeExists(Message.SUCCESS))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/student/student-card/" + studentId));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void testDeleteStudentAlert() throws Exception {
        int alertId = 1;
        mockMvc.perform(MockMvcRequestBuilders.post("/student/remove-alert/{alertId}", alertId).with(csrf().asHeader()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.flash().attributeExists(Message.SUCCESS))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/student/alert"));
    }

    @Test
    @WithMockUser(roles = { "ADMIN", "STUDENT", "TEACHER", "STAFF" })
    void testGetAllStudentsList() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/student/student-list"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("students"))
                .andExpect(MockMvcResultMatchers.view().name("student/student-list"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteStudent() throws Exception {
        int studentId = 1;
        mockMvc.perform(MockMvcRequestBuilders.post("/student/delete/{studentId}", studentId).with(csrf().asHeader()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.flash().attributeExists(Message.SUCCESS))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/student/student-list"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testOpenStudentCard_WhenStudentExists() throws Exception {
        Faculty facultyGrifindor = new Faculty("Grifindor");
        Group group = new Group("Group A", facultyGrifindor);
        Student student = new Student("Harry", "Potter", true, "potter@mail.com", "1234", group);
        student.setId(1);

        when(studentService.findStudentById(student.getId())).thenReturn(Optional.of(student));

        mockMvc.perform(MockMvcRequestBuilders.get("/student/student-card/{studentId}", student.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("student/student-card"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("student"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("courses"))
                .andExpect(MockMvcResultMatchers.model().attribute("student", Matchers.sameInstance(student)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testOpenStudentCard_WhenStudentDoesNotExist() throws Exception {
        int studentId = 999;

        when(studentService.findStudentById(studentId)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/student/student-card/{studentId}", studentId))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/student/student-list"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testRemoveStudentFromCourse() throws Exception {
        int studentId = 1;
        Course course = new Course("Herbology");

        mockMvc.perform(MockMvcRequestBuilders
                .post("/student/remove-course/{studentId}/{courseName}", studentId, course.getCourseName())
                .with(csrf().asHeader())).andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.flash().attributeExists(Message.SUCCESS))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/student/student-card/" + studentId));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAddStudentToTheCourse_Success() throws Exception {
        int studentId = 1;
        String courseName = "Math";

        mockMvc.perform(MockMvcRequestBuilders.post("/student/assign-course")
                .param("studentId", String.valueOf(studentId)).param("courseName", courseName).with(csrf().asHeader()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.flash().attributeExists(Message.SUCCESS))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/student/student-card/" + studentId));

        verify(studentService, times(1)).addStudentToTheCourse(studentId, courseName);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAddStudentToTheCourse_Failure_IllegalStateException() throws Exception {
        int studentId = 1;
        String courseName = "Math";

        doThrow(new IllegalStateException("Error message")).when(studentService).addStudentToTheCourse(studentId,
                courseName);

        mockMvc.perform(MockMvcRequestBuilders.post("/student/assign-course")
                .param("studentId", String.valueOf(studentId)).param("courseName", courseName).with(csrf().asHeader()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.flash().attributeExists(Message.ERROR))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/student/student-card/" + studentId));

        verify(studentService, times(1)).addStudentToTheCourse(studentId, courseName);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAddStudentToTheCourse_Failure_NoSuchElementException() throws Exception {
        int studentId = 1;
        String courseName = "Math";

        doThrow(new NoSuchElementException("Error message")).when(studentService).addStudentToTheCourse(studentId,
                courseName);

        mockMvc.perform(MockMvcRequestBuilders.post("/student/assign-course")
                .param("studentId", String.valueOf(studentId)).param("courseName", courseName).with(csrf().asHeader()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.flash().attributeExists(Message.ERROR))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/student/student-card/" + studentId));

        verify(studentService, times(1)).addStudentToTheCourse(studentId, courseName);
    }

    @Test
    @WithMockUser(roles = { "ADMIN", "STUDENT", "TEACHER", "STAFF" })
    void testSearchStudent_WhenSearchTypeIsStudent() throws Exception {
        Faculty facultyGrifindor = new Faculty("Grifindor");
        Group group = new Group("Group A", facultyGrifindor);
        Student student = new Student("Harry", "Potter", true, "potter@mail.com", "1234", group);
        student.setId(1);

        when(studentService.findStudentById(student.getId())).thenReturn(Optional.of(student));

        mockMvc.perform(MockMvcRequestBuilders.get("/student/search-result").param("searchType", "student")
                .param("studentId", String.valueOf(student.getId()))).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("student/student-list"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("students"))
                .andExpect(MockMvcResultMatchers.model().attribute("students", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.model().attribute("students", Matchers.contains(student)));
    }

    @Test
    @WithMockUser(roles = { "ADMIN", "STUDENT", "TEACHER", "STAFF" })
    void testSearchStudent_WhenSearchTypeIsFirstNameAndLastName() throws Exception {
        Faculty facultyGrifindor = new Faculty("Grifindor");
        Group group = new Group("Group A", facultyGrifindor);
        Student student = new Student("Harry", "Potter", true, "potter@mail.com", "1234", group);
        student.setId(1);

        when(studentService.findStudentByName(student.getFirstName(), student.getLastName()))
                .thenReturn(Collections.singletonList(student));

        mockMvc.perform(MockMvcRequestBuilders.get("/student/search-result").param("searchType", "firstNameAndLastName")
                .param("firstName", student.getFirstName()).param("lastName", student.getLastName()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("student/student-list"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("students"))
                .andExpect(MockMvcResultMatchers.model().attribute("students", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.model().attribute("students", Matchers.contains(student)));
    }

    @Test
    @WithMockUser(roles = { "ADMIN", "STUDENT", "TEACHER", "STAFF" })
    void testSearchStudent_WhenSearchTypeIsFaculty() throws Exception {
        Faculty faculty = new Faculty("Grifindor");
        Group group = new Group("Group A", faculty);
        Student student1 = new Student("Harry", "Potter", true, "potter@mail.com", "1234", group);
        student1.setId(1);

        Student student2 = new Student("Ron", "Wesley", true, "ronald@mail.com", "1234", group);
        student2.setId(2);
        List<Student> students = Arrays.asList(student1, student2);

        when(studentService.findAllByGroupFacultyFacultyName(faculty.getFacultyName())).thenReturn(students);

        mockMvc.perform(MockMvcRequestBuilders.get("/student/search-result").param("searchType", "faculty")
                .param("facultyName", faculty.getFacultyName())).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("student/student-list"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("students"))
                .andExpect(MockMvcResultMatchers.model().attribute("students", Matchers.hasSize(2)))
                .andExpect(MockMvcResultMatchers.model().attribute("students", Matchers.contains(student1, student2)));
    }

    @Test
    @WithMockUser(roles = { "ADMIN", "STUDENT", "TEACHER", "STAFF" })
    void testSearchStudent_WhenSearchTypeIsGroup() throws Exception {
        Faculty faculty = new Faculty("Grifindor");
        Group group = new Group("Group A", faculty);
        Student student1 = new Student("Harry", "Potter", true, "potter@mail.com", "1234", group);
        student1.setId(1);

        Student student2 = new Student("Ron", "Wesley", true, "ronald@mail.com", "1234", group);
        student2.setId(2);
        List<Student> students = Arrays.asList(student1, student2);

        when(studentService.findAllByGroupName(group.getGroupName())).thenReturn(students);

        mockMvc.perform(MockMvcRequestBuilders.get("/student/search-result").param("searchType", "group")
                .param("groupName", group.getGroupName())).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("student/student-list"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("students"))
                .andExpect(MockMvcResultMatchers.model().attribute("students", Matchers.hasSize(2)))
                .andExpect(MockMvcResultMatchers.model().attribute("students", Matchers.contains(student1, student2)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testShowCreateStudentForm() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/student/create-student"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("student/create-student"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateStudent() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/student/create-student").with(csrf().asHeader()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.flash().attributeCount(1))
                .andExpect(MockMvcResultMatchers.flash().attributeExists(Message.SUCCESS));
    }
}
