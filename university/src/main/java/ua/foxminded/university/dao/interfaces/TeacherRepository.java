package ua.foxminded.university.dao.interfaces;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ua.foxminded.university.dao.entities.Teacher;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Integer> {

	@Query("""
			SELECT t FROM Teacher t
			     JOIN TeachersCourses tc ON t.id = tc.teacherId
			     JOIN Course c ON c.id = tc.courseId WHERE c.courseName = :courseName
			""")
	List<Teacher> findTeachersRelatedToCourse(@Param("courseName") String courseName);
}
