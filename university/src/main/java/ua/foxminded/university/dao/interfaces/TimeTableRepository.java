package ua.foxminded.university.dao.interfaces;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ua.foxminded.university.dao.entities.Group;
import ua.foxminded.university.dao.entities.Student;
import ua.foxminded.university.dao.entities.Teacher;
import ua.foxminded.university.dao.entities.TimeTable;

@Repository
public interface TimeTableRepository extends JpaRepository<TimeTable, Integer> {

	public List<TimeTable> findByStudent(Student student);

	public List<TimeTable> findByTeacher(Teacher teacher);

	public List<TimeTable> findByGroup(Group group);

	@Query("SELECT t FROM TimeTable t JOIN FETCH t.group JOIN FETCH t.classRoom JOIN FETCH t.teacher JOIN FETCH t.course WHERE t.date >= ?1 AND t.date <= ?2")
	List<TimeTable> findByDate(LocalDate dateFrom, LocalDate dateTo);

	@Query("SELECT t FROM TimeTable t WHERE t.date>= ?1 AND t.date <= ?2 AND t.teacher = ?3")
	List<TimeTable> findByDateAndTeacher(LocalDate dateFrom, LocalDate dateTo, Teacher teacher);

	@Query("SELECT t FROM TimeTable t WHERE t.date >= ?1 AND t.date <= ?2 AND t.group = ?3")
	List<TimeTable> findByDateAndGroup(LocalDate dateFrom, LocalDate dateTo, Group group);
}
