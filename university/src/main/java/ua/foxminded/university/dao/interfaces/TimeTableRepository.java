package ua.foxminded.university.dao.interfaces;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ua.foxminded.university.dao.entities.ClassRoom;
import ua.foxminded.university.dao.entities.Group;
import ua.foxminded.university.dao.entities.Teacher;
import ua.foxminded.university.dao.entities.TimeTable;

@Repository
public interface TimeTableRepository extends JpaRepository<TimeTable, Integer> {

	List<TimeTable> findByTeacher(Teacher teacher);

	List<TimeTable> findByGroup(Group group);

	@Query("""
			    SELECT t FROM TimeTable t
			    JOIN t.course c
			    JOIN c.students s
			    WHERE s.id = :studentId
			""")
	List<TimeTable> findByStudent(@Param("studentId") int studentId);

	@Query("SELECT t FROM TimeTable t JOIN FETCH t.group JOIN FETCH t.classRoom JOIN FETCH t.teacher JOIN FETCH t.course WHERE t.date >= ?1 AND t.date <= ?2")
	List<TimeTable> findByDate(LocalDate dateFrom, LocalDate dateTo);

	@Query("SELECT t FROM TimeTable t WHERE t.date>= ?1 AND t.date <= ?2 AND t.teacher = ?3")
	List<TimeTable> findByDateAndTeacher(LocalDate dateFrom, LocalDate dateTo, Teacher teacher);

	@Query("SELECT t FROM TimeTable t WHERE t.date >= ?1 AND t.date <= ?2 AND t.group = ?3")
	List<TimeTable> findByDateAndGroup(LocalDate dateFrom, LocalDate dateTo, Group group);

	@Query("""
			    SELECT t FROM TimeTable t
			    JOIN t.course c
			    JOIN c.students s
			    WHERE s.id = :studentId
			    AND t.date BETWEEN :dateFrom AND :dateTo
			""")
	List<TimeTable> findByDateAndStudent(@Param("dateFrom") LocalDate dateFrom, @Param("dateTo") LocalDate dateTo,
			@Param("studentId") int studentId);

	boolean existsByDateAndTimeFromAndTimeToAndClassRoom(LocalDate date, LocalTime timeFrom, LocalTime timeTo,
			ClassRoom classRoom);

	@Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Student s JOIN s.courses c WHERE s.id = :studentId")
	boolean studentIsAssignedToAnyCourse(@Param("studentId") int studentId);

}
