package ua.foxminded.university.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.foxminded.university.entities.Student;

public interface StudentRepository extends JpaRepository<Student, Integer> {

}
