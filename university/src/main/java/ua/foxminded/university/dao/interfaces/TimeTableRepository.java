package ua.foxminded.university.dao.interfaces;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.foxminded.university.dao.entities.Group;
import ua.foxminded.university.dao.entities.Student;
import ua.foxminded.university.dao.entities.Teacher;
import ua.foxminded.university.dao.entities.TimeTable;

@Repository
public interface TimeTableRepository extends JpaRepository<TimeTable, Integer> {

	public Optional<TimeTable> findByStudent(Student student);

	public Optional<TimeTable> findByTeacher(Teacher teacher);

	public Optional<TimeTable> findByGroup(Group group);
}
