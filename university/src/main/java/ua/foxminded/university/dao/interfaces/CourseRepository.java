package ua.foxminded.university.dao.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ua.foxminded.university.dao.entities.Course;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {

}
