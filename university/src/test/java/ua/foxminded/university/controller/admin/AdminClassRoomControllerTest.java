package ua.foxminded.university.controller.admin;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ua.foxminded.university.dao.service.ClassRoomService;

@WebMvcTest(AdminClassRoomController.class)
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
	void testDeleteClassRoom() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/admin/classroom/delete/{classroomId}", 1))
				.andExpect(MockMvcResultMatchers.status()
						.is3xxRedirection())
				.andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"))
				.andExpect(MockMvcResultMatchers.redirectedUrl("/admin/classroom/edit-classroom-list"));
	}

	@Test
	void testShowCreateClassRoomForm() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/admin/classroom/create-classroom"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("admin/classroom/create-classroom"));
	}
}
