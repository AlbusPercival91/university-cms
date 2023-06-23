package ua.foxminded.university.dao.interfaces;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ua.foxminded.university.dao.entities.Course;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {

	Optional<Course> findByCourseName(String courseName);

	@Query("""
			SELECT c FROM Course c
			     JOIN c.teachers t
			     WHERE t.id = :teacherId
			""")
	List<Course> findCoursesRelatedToTeacher(@Param("teacherId") int teacherId);

	@Query("""
			SELECT c FROM Course c
			     JOIN c.students s
			     WHERE s.id = :studentId
			""")
	List<Course> findCoursesRelatedToStudent(@Param("studentId") int studentId);

}
