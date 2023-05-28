package ua.foxminded.university.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.foxminded.university.entities.Teacher;

public interface TeacherRepository extends JpaRepository<Teacher, Integer> {

}
