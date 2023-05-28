package ua.foxminded.university.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.foxminded.university.entities.Course;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {

}
