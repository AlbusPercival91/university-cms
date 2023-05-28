package ua.foxminded.university.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.foxminded.university.entities.Department;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Integer> {

}
