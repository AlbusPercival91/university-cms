package ua.foxminded.university.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.foxminded.university.entities.Course;

public interface CourseRepository extends JpaRepository<Course, Integer> {

}
