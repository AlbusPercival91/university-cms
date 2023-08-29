package ua.foxminded.university.controller;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import java.util.Collections;
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
import ua.foxminded.university.dao.entities.ClassRoom;
import ua.foxminded.university.dao.service.ClassRoomService;
import ua.foxminded.university.validation.ControllerBindingValidator;

@WebMvcTest({ ClassRoomController.class, ControllerBindingValidator.class })
@ActiveProfiles("test-container")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ClassRoomControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ClassRoomService classRoomService;

	@Test
	@WithMockUser(roles = { "ADMIN", "STUDENT", "TEACHER", "STAFF" })
	void testGetAllClassRoom() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/classroom/classroom-list"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.model().attributeExists("classrooms"))
				.andExpect(MockMvcResultMatchers.view().name("classroom/classroom-list"));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void testDeleteClassRoom() throws Exception {
		int classRoomId = 1;
		mockMvc.perform(
				MockMvcRequestBuilders.post("/classroom/delete/{classroomId}", classRoomId).with(csrf().asHeader()))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"))
				.andExpect(MockMvcResultMatchers.redirectedUrl("/classroom/classroom-list"));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void testShowCreateClassRoomForm() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/classroom/create-classroom"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("classroom/create-classroom"));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void testCreateClassRoom() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/classroom/create-classroom").with(csrf().asHeader()))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.flash().attributeCount(1))
				.andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
	}

	@Test
	@WithMockUser(roles = { "ADMIN", "STUDENT", "TEACHER", "STAFF" })
	void testSearchClassRoom_WhenSearchTypeIsClassroom() throws Exception {
		ClassRoom classRoom = new ClassRoom();
		classRoom.setId(1);
		when(classRoomService.findClassRoomById(classRoom.getId())).thenReturn(Optional.of(classRoom));

		mockMvc.perform(MockMvcRequestBuilders.get("/classroom/search-result").param("searchType", "classroom")
				.param("classroomId", String.valueOf(classRoom.getId())))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("classroom/classroom-list"))
				.andExpect(MockMvcResultMatchers.model().attributeExists("classrooms"))
				.andExpect(MockMvcResultMatchers.model().attribute("classrooms", Matchers.hasSize(1)))
				.andExpect(MockMvcResultMatchers.model().attribute("classrooms", Matchers.contains(classRoom)));
	}

	@Test
	@WithMockUser(roles = { "ADMIN", "STUDENT", "TEACHER", "STAFF" })
	void testSearchClassRoom_WhenSearchTypeIsStreet() throws Exception {
		ClassRoom classRoom = new ClassRoom("Example Street", 12, 14);

		when(classRoomService.findClassRoomsByStreet(classRoom.getStreet()))
				.thenReturn(Collections.singletonList(classRoom));

		mockMvc.perform(MockMvcRequestBuilders.get("/classroom/search-result").param("searchType", "street")
				.param("street", classRoom.getStreet())).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("classroom/classroom-list"))
				.andExpect(MockMvcResultMatchers.model().attributeExists("classrooms"))
				.andExpect(MockMvcResultMatchers.model().attribute("classrooms", Matchers.hasSize(1)))
				.andExpect(MockMvcResultMatchers.model().attribute("classrooms", Matchers.contains(classRoom)));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void testOpenClassRoomCard_WhenClassRoomExists() throws Exception {
		ClassRoom classRoom = new ClassRoom("Example Street", 12, 14);
		classRoom.setId(1);

		when(classRoomService.findClassRoomById(classRoom.getId())).thenReturn(Optional.of(classRoom));

		mockMvc.perform(MockMvcRequestBuilders.get("/classroom/classroom-card/{classroomId}", classRoom.getId()))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("classroom/classroom-card"))
				.andExpect(MockMvcResultMatchers.model().attributeExists("classroom"))
				.andExpect(MockMvcResultMatchers.model().attribute("classroom", Matchers.sameInstance(classRoom)));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void testOpenClassRoomCard_WhenClassRoomDoesNotExist() throws Exception {
		int classroomId = 999;

		when(classRoomService.findClassRoomById(classroomId)).thenReturn(Optional.empty());

		mockMvc.perform(MockMvcRequestBuilders.get("/classroom/classroom-card/{classroomId}", classroomId))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.redirectedUrl("/classroom/classroom-list"));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void testUpdateClassRoom() throws Exception {
		int classRoomId = 1;
		ClassRoom updatedClassRomm = new ClassRoom();
		updatedClassRomm.setId(classRoomId);

		when(classRoomService.updateClassRoomById(classRoomId, updatedClassRomm)).thenReturn(updatedClassRomm);

		mockMvc.perform(MockMvcRequestBuilders.post("/classroom/edit-classroom/{classroomId}", classRoomId)
				.flashAttr("classroom", updatedClassRomm).with(csrf().asHeader()))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"))
				.andExpect(MockMvcResultMatchers.redirectedUrl("/classroom/classroom-card/" + classRoomId));
	}
}
