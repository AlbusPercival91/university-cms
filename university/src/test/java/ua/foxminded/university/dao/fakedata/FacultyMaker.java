package ua.foxminded.university.dao.fakedata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ua.foxminded.university.dao.entities.Faculty;

public class FacultyMaker {

	public final Set<String> facultyNames() {
		return new HashSet<>(Arrays.asList("Faculty of Engineering", "Faculty of Medicine", "Faculty of Law",
				"Faculty of Bussines Administration", "Faculty of Social Sciences", "Faculty of Natural Sciences",
				"Faculty of Humanities", "Faculty of Education", "Faculty of Fine Arts",
				"Faculty of Information Technology"));
	}

	public List<Faculty> generateFaculties() {
		List<Faculty> list = new ArrayList<>();

		for (String s : facultyNames()) {
			Faculty faculty = new Faculty(s);
			list.add(faculty);
		}
		return list;
	}
}
