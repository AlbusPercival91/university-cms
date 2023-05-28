package ua.foxminded.university.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.foxminded.university.entities.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {

}
