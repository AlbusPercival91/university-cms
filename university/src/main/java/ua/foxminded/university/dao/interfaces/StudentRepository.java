package ua.foxminded.university.dao.interfaces;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ua.foxminded.university.dao.entities.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {

	@Modifying
	@Query("""
			 INSERT INTO StudentsCourses (studentId, courseId)
			     SELECT s.id, c.id FROM Student s, Course c
			     WHERE s.id = :studentId AND c.courseName = :courseName
			""")
	int addStudentToTheCourse(@Param("studentId") int studentId, @Param("courseName") String courseName);

	@Modifying
	@Query("""
			 DELETE FROM StudentsCourses sc
			     WHERE sc.studentId = :studentId
			     AND sc.courseId IN (SELECT c.id FROM Course c WHERE c.courseName = :courseName)
			""")
	int removeStudentFromCourse(@Param("studentId") int studentId, @Param("courseName") String courseName);

	@Query("""
			SELECT s FROM Student s
			     JOIN StudentsCourses sc ON s.id = sc.studentId
			     JOIN Course c ON c.id = sc.courseId WHERE c.courseName = :courseName
			""")
	List<Student> findStudentsRelatedToCourse(@Param("courseName") String courseName);

	List<Student> findAllByGroupGroupName(String groupName);

	List<Student> findAllByGroupFacultyFacultyName(String facultyName);

	Optional<Student> findStudentByFirstNameAndLastName(String firstName, String lastName);
}
