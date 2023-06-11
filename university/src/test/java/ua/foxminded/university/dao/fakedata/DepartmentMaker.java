package ua.foxminded.university.dao.fakedata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import ua.foxminded.university.dao.entities.Department;

public class DepartmentMaker {

	@Autowired
	private FacultyMaker facultyMaker;

	public final Set<String> departmentNames() {
		return new HashSet<>(Arrays.asList("Department of ComputerSciance and Engineering", "Department of Psychology",
				"Department of Legal Studies", "Department of Human Resources", "Department of Sociology",
				"Department of Mathematics", "Department of History", "Department of Special Education",
				"Department of Design", "Department of Software Engineering"));
	}

	public List<Department> generateDepartments() {
		List<Department> departmentList = new ArrayList<>();

		for (int i = 0; i < facultyMaker.generateFaculties().size(); i++) {
			Department department = new Department(departmentNames().toArray()[i].toString(),
					facultyMaker.generateFaculties().get(i));
			departmentList.add(department);
		}
		return departmentList;
	}
}
