package ua.foxminded.university.dao.interfaces;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ua.foxminded.university.dao.entities.Teacher;
import ua.foxminded.university.dao.entities.TeacherAlerts;

public interface TeacherAlertsRepository extends JpaRepository<TeacherAlerts, Integer> {

    List<TeacherAlerts> findByTeacher(Teacher teacher);
}
