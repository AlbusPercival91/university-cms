package ua.foxminded.university.dao.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import ua.foxminded.university.dao.entities.Teacher;
import ua.foxminded.university.dao.entities.Alert;
import ua.foxminded.university.dao.entities.Student;
import ua.foxminded.university.dao.interfaces.AlertRepository;

@RequiredArgsConstructor
@Service
@Transactional
public class AlertService {
    private final AlertRepository teacherAlertsRepository;

    public void createTeacherAlert(LocalDate date, LocalTime time, Teacher teacher, String message) {
        Alert alert = new Alert(date, time, teacher, message);
        teacherAlertsRepository.save(alert);
    }

    public void createStudentAlert(LocalDate date, LocalTime time, Student student, String message) {
        Alert alert = new Alert(date, time, student, message);
        teacherAlertsRepository.save(alert);
    }

    public List<Alert> getAllTeacherAlerts(Teacher teacher) {
        return teacherAlertsRepository.findByTeacher(teacher);
    }

    public List<Alert> getAllStudentAlerts(Student student) {
        return teacherAlertsRepository.findByStudent(student);
    }
}
