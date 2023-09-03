package ua.foxminded.university.dao.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import ua.foxminded.university.dao.entities.Teacher;
import ua.foxminded.university.dao.entities.TeacherAlerts;
import ua.foxminded.university.dao.interfaces.TeacherAlertsRepository;

@RequiredArgsConstructor
@Service
@Transactional
public class TeacherAlertsService {
    private final TeacherAlertsRepository teacherAlertsRepository;

    public void createTeacherAlert(LocalDate date, LocalTime time, Teacher teacher, String message) {
        TeacherAlerts alert = new TeacherAlerts(date, time, teacher, message);
        teacherAlertsRepository.save(alert);
    }

    public List<TeacherAlerts> getAllAlertsByTeacher(Teacher teacher) {
        return teacherAlertsRepository.findByTeacher(teacher);
    }
}
