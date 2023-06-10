package ua.foxminded.university.dao.fakedata;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class DepartmentMaker {

	public final Set<String> generateDepartments() {
		return new HashSet<>(Arrays.asList("Department of ComputerSciance and Engineering", "Department of Psychology",
				"Department of Legal Studies", "Department of Human Resources", "Department of Sociology",
				"Department of Mathematics", "Department of History", "Department of Special Education",
				"Department of Design", "Department of Software Engineering"));
	}
}
