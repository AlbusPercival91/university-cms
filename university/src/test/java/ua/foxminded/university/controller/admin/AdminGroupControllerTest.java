package ua.foxminded.university.controller.admin;

import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ua.foxminded.university.dao.entities.Faculty;
import ua.foxminded.university.dao.entities.Group;
import ua.foxminded.university.dao.service.FacultyService;
import ua.foxminded.university.dao.service.GroupService;

@WebMvcTest(AdminGroupController.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test-container")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class AdminGroupControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private GroupService groupService;

	@MockBean
	private FacultyService facultyService;

	@Test
	void testGetAllGroupListAsAdmin() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/admin/group/edit-group-list"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.model().attributeExists("groups"))
				.andExpect(MockMvcResultMatchers.view().name("admin/group/edit-group-list"));
	}

	@Test
	void testDeleteGroup() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/admin/group/delete/{grouptId}", 1))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"))
				.andExpect(MockMvcResultMatchers.redirectedUrl("/admin/group/edit-group-list"));
	}

	@Test
	void testShowCreateGroupForm() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/admin/group/create-group"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("admin/group/create-group"));
	}

	@Test
	void testCreateGroup() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/admin/group/create-group"))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.flash().attributeCount(1))
				.andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
	}

	@Test
	void testSearchGroupAsAdmin_WhenSearchTypeIsGroup() throws Exception {
		Faculty faculty = new Faculty("Faculty A");
		Group group = new Group("Group A", faculty);
		group.setId(1);

		when(groupService.findGroupById(1)).thenReturn(Optional.of(group));

		mockMvc.perform(MockMvcRequestBuilders.get("/admin/group/search-result").param("searchType", "group")
				.param("groupId", "1")).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("admin/group/edit-group-list"))
				.andExpect(MockMvcResultMatchers.model().attributeExists("groups"))
				.andExpect(MockMvcResultMatchers.model().attribute("groups", Matchers.hasSize(1)))
				.andExpect(MockMvcResultMatchers.model().attribute("groups", Matchers.contains(group)));
	}

	@Test
	void testSearchGroupAsAdmin_WhenSearchTypeIsGroupName() throws Exception {
		Faculty faculty = new Faculty("Faculty A");
		Group group = new Group("Group A", faculty);
		group.setId(1);

		when(groupService.findGroupByGroupName(group.getGroupName())).thenReturn(Collections.singletonList(group));

		mockMvc.perform(MockMvcRequestBuilders.get("/admin/group/search-result").param("searchType", "groupName")
				.param("groupName", group.getGroupName())).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("admin/group/edit-group-list"))
				.andExpect(MockMvcResultMatchers.model().attributeExists("groups"))
				.andExpect(MockMvcResultMatchers.model().attribute("groups", Matchers.hasSize(1)))
				.andExpect(MockMvcResultMatchers.model().attribute("groups", Matchers.contains(group)));
	}

	@Test
	void testSearchGroupAsAdmin_WhenSearchTypeIsFaculty() throws Exception {
		Faculty faculty = new Faculty("Faculty A");
		Group group1 = new Group("Group A", faculty);
		group1.setId(1);
		Group group2 = new Group("Group B", faculty);
		group2.setId(2);
		List<Group> groups = Arrays.asList(group1, group2);

		when(groupService.findAllByFacultyName(faculty.getFacultyName())).thenReturn(groups);

		mockMvc.perform(MockMvcRequestBuilders.get("/admin/group/search-result").param("searchType", "faculty")
				.param("facultyName", faculty.getFacultyName())).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("admin/group/edit-group-list"))
				.andExpect(MockMvcResultMatchers.model().attributeExists("groups"))
				.andExpect(MockMvcResultMatchers.model().attribute("groups", Matchers.hasSize(2)))
				.andExpect(MockMvcResultMatchers.model().attribute("groups", Matchers.contains(group1, group2)));
	}

	@Test
	void testOpenGrouptCard_WhenGroupExists() throws Exception {
		Faculty faculty = new Faculty("Faculty A");
		Group group = new Group("Group A", faculty);
		group.setId(1);

		when(groupService.findGroupById(group.getId())).thenReturn(Optional.of(group));

		mockMvc.perform(MockMvcRequestBuilders.get("/admin/group/group-card/{groupId}", group.getId()))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("admin/group/group-card"))
				.andExpect(MockMvcResultMatchers.model().attributeExists("group"))
				.andExpect(MockMvcResultMatchers.model().attribute("group", Matchers.sameInstance(group)));
	}

	@Test
	void testOpenGroupCard_WhenGroupDoesNotExist() throws Exception {
		int groupId = 999;

		when(groupService.findGroupById(groupId)).thenReturn(Optional.empty());

		mockMvc.perform(MockMvcRequestBuilders.get("/admin/group/group-card/{groupId}", groupId))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.redirectedUrl("/admin/group/edit-group-list"));
	}
}
