package ua.foxminded.university.dao.interfaces;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.foxminded.university.dao.entities.Department;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Integer> {

    List<Department> findAllByFacultyFacultyNameOrderByIdAsc(String facultyName);

    List<Department> findDepartmentByNameOrderByIdAsc(String name);
}
