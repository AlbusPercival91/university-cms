package ua.foxminded.university.dao.interfaces;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ua.foxminded.university.dao.entities.Teacher;
import ua.foxminded.university.dao.entities.Admin;
import ua.foxminded.university.dao.entities.Alert;
import ua.foxminded.university.dao.entities.Staff;
import ua.foxminded.university.dao.entities.Student;

public interface AlertRepository extends JpaRepository<Alert, Integer> {

    List<Alert> findByTeacher(Teacher teacher);

    List<Alert> findByStudent(Student student);

    List<Alert> findByAdmin(Admin admin);

    List<Alert> findByStaff(Staff staff);
}
