package ua.foxminded.university.controller.admin;

import static org.mockito.Mockito.when;
import java.util.Collections;
import java.util.Optional;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ua.foxminded.university.dao.entities.ClassRoom;
import ua.foxminded.university.dao.service.ClassRoomService;

@WebMvcTest(AdminClassRoomController.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test-container")
class AdminClassRoomControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ClassRoomService classRoomService;

	@Test
	void testGetAllClassRoomListAsAdmin() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/admin/classroom/edit-classroom-list"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.model().attributeExists("classrooms"))
				.andExpect(MockMvcResultMatchers.view().name("admin/classroom/edit-classroom-list"));
	}

	@Test
	void testDeleteClassRoom_WhenClassRoomExists() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/admin/classroom/delete/{classroomId}", 1))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"))
				.andExpect(MockMvcResultMatchers.redirectedUrl("/admin/classroom/edit-classroom-list"));
	}

	@Test
	void testShowCreateClassRoomForm() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/admin/classroom/create-classroom"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("admin/classroom/create-classroom"));
	}

	@Test
	void testCreateClassRoom() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/admin/classroom/create-classroom"))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.flash().attributeCount(1))
				.andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
	}

	@Test
	void testSearchClassRoomAsAdmin_WhenSearchTypeIsClassroom() throws Exception {
		ClassRoom classRoom = new ClassRoom();
		classRoom.setId(1);
		when(classRoomService.findClassRoomById(1)).thenReturn(Optional.of(classRoom));

		mockMvc.perform(MockMvcRequestBuilders.get("/admin/classroom/search-result").param("searchType", "classroom")
				.param("classroomId", "1")).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("admin/classroom/edit-classroom-list"))
				.andExpect(MockMvcResultMatchers.model().attributeExists("classrooms"))
				.andExpect(MockMvcResultMatchers.model().attribute("classrooms", Matchers.hasSize(1)))
				.andExpect(MockMvcResultMatchers.model().attribute("classrooms", Matchers.contains(classRoom)));
	}

	@Test
	void testSearchClassRoomAsAdmin_WhenSearchTypeIsStreet() throws Exception {
		ClassRoom classRoom = new ClassRoom("Example Street", 12, 14);

		when(classRoomService.findClassRoomsByStreet(classRoom.getStreet()))
				.thenReturn(Collections.singletonList(classRoom));

		mockMvc.perform(MockMvcRequestBuilders.get("/admin/classroom/search-result").param("searchType", "street")
				.param("street", classRoom.getStreet())).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("admin/classroom/edit-classroom-list"))
				.andExpect(MockMvcResultMatchers.model().attributeExists("classrooms"))
				.andExpect(MockMvcResultMatchers.model().attribute("classrooms", Matchers.hasSize(1)))
				.andExpect(MockMvcResultMatchers.model().attribute("classrooms", Matchers.contains(classRoom)));
	}

	@Test
	void testOpenClassRoomCard_WhenClassRoomExists() throws Exception {
		ClassRoom classRoom = new ClassRoom("Example Street", 12, 14);
		classRoom.setId(1);

		when(classRoomService.findClassRoomById(classRoom.getId())).thenReturn(Optional.of(classRoom));

		mockMvc.perform(MockMvcRequestBuilders.get("/admin/classroom/classroom-card/{classroomId}", classRoom.getId()))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("admin/classroom/classroom-card"))
				.andExpect(MockMvcResultMatchers.model().attributeExists("classroom"))
				.andExpect(MockMvcResultMatchers.model().attribute("classroom", Matchers.sameInstance(classRoom)));
	}

	@Test
	void testOpenClassRoomCard_WhenClassRoomDoesNotExist() throws Exception {
		int classroomId = 999;

		when(classRoomService.findClassRoomById(1)).thenReturn(Optional.empty());

		mockMvc.perform(MockMvcRequestBuilders.get("/admin/classroom/classroom-card/{classroomId}", classroomId))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.redirectedUrl("/admin/classroom/edit-classroom-list"));
	}
}
