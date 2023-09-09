package ua.foxminded.university.dao.interfaces;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ua.foxminded.university.dao.entities.Teacher;
import ua.foxminded.university.dao.entities.Admin;
import ua.foxminded.university.dao.entities.Alert;
import ua.foxminded.university.dao.entities.Staff;
import ua.foxminded.university.dao.entities.Student;

public interface AlertRepository extends JpaRepository<Alert, Integer> {

    List<Alert> findByTeacherOrderByTimestampDesc(Teacher teacher);

    List<Alert> findByStudentOrderByTimestampDesc(Student student);

    List<Alert> findByAdminOrderByTimestampDesc(Admin admin);

    List<Alert> findByStaffOrderByTimestampDesc(Staff staff);

    List<Alert> findByTeacherIdAndTimestampBetweenOrderByTimestampDesc(int teacherId, LocalDateTime from,
            LocalDateTime to);

    List<Alert> findByStudentIdAndTimestampBetweenOrderByTimestampDesc(int studentId, LocalDateTime from,
            LocalDateTime to);

    List<Alert> findByAdminIdAndTimestampBetweenOrderByTimestampDesc(int adminId, LocalDateTime from, LocalDateTime to);

    List<Alert> findByStaffIdAndTimestampBetweenOrderByTimestampDesc(int staffId, LocalDateTime from, LocalDateTime to);

}
