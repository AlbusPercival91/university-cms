package ua.foxminded.university.dao.fakedata;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class FacultyMaker {

	public final Set<String> generateFaculties() {
		return new HashSet<>(Arrays.asList("Faculty of Engineering", "Faculty of Medicine", "Faculty of Law",
				"Faculty of Bussines Administration", "Faculty of Social Sciences", "Faculty of Natural Sciences",
				"Faculty of Humanities", "Faculty of Education", "Faculty of Fine Arts",
				"Faculty of Information Technology"));
	}
}
