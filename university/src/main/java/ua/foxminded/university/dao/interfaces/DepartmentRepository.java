package ua.foxminded.university.dao.interfaces;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.foxminded.university.dao.entities.Department;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Integer> {

	List<Department> findAllByFacultyFacultyName(String facultyName);

	Optional<Department> findDepartmentByName(String name);
}
