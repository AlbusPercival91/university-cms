package ua.foxminded.university.dao.interfaces;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ua.foxminded.university.dao.entities.Teacher;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Integer> {

    @Modifying
    @Query("""
             INSERT INTO TeachersCourses (teacherId, courseId)
                 SELECT t.id, c.id FROM Teacher t, Course c
                 WHERE t.id = :teacherId AND c.courseName = :courseName
            """)
    int addTeacherToTheCourse(@Param("teacherId") int teacherId, @Param("courseName") String courseName);

    @Modifying
    @Query("""
             DELETE FROM TeachersCourses tc
                 WHERE tc.teacherId = :teacherId
                 AND tc.courseId IN (SELECT c.id FROM Course c WHERE c.courseName = :courseName)
            """)
    int removeTeacherFromCourse(@Param("teacherId") int teacherId, @Param("courseName") String courseName);

    List<Teacher> findTeacherByFirstNameAndLastNameOrderByIdAsc(String firstName, String lastName);

    @Query("""
            SELECT t FROM Teacher t
                 JOIN TeachersCourses tc ON t.id = tc.teacherId
                 JOIN Course c ON c.id = tc.courseId WHERE c.courseName = :courseName
            """)
    List<Teacher> findTeachersRelatedToCourseOrderByIdAsc(@Param("courseName") String courseName);

    /*
     * return all Teachers in Faculty
     */
    List<Teacher> findAllByDepartmentFacultyFacultyNameOrderByIdAsc(String facultyName);

    /*
     * return all Teachers in defined Department of special Faculty
     */
    List<Teacher> findAllByDepartmentIdAndDepartmentFacultyIdOrderByIdAsc(int departmentId, int facultyId);

    Optional<Teacher> findByEmail(String email);
}
