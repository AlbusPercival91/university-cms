package ua.foxminded.university.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.foxminded.university.entities.Department;

public interface DepartmentRepository extends JpaRepository<Department, Integer> {

}
