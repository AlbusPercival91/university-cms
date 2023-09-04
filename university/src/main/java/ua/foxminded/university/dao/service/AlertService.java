package ua.foxminded.university.dao.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import ua.foxminded.university.dao.entities.Teacher;
import ua.foxminded.university.dao.entities.Alert;
import ua.foxminded.university.dao.entities.Faculty;
import ua.foxminded.university.dao.entities.Student;
import ua.foxminded.university.dao.interfaces.AlertRepository;

@RequiredArgsConstructor
@Service
@Transactional
public class AlertService {
    private final AlertRepository alertRepository;

    public void createTeacherAlert(LocalDate date, LocalTime time, Teacher teacher, String message) {
        Alert alert = new Alert(date, time, teacher, message);
        alertRepository.save(alert);
    }

    public void createStudentAlert(LocalDate date, LocalTime time, Student student, String message) {
        Alert alert = new Alert(date, time, student, message);
        alertRepository.save(alert);
    }

    public void createFacultyAlert(LocalDate date, LocalTime time, Faculty faculty, String message) {
        List<Alert> alerts = faculty.getGroups().stream()
                .flatMap(group -> group.getStudents().stream().map(student -> new Alert(date, time, student, message)))
                .collect(Collectors.toList());

        alerts.addAll(faculty.getDepartments().stream().flatMap(
                department -> department.getTeachers().stream().map(teacher -> new Alert(date, time, teacher, message)))
                .toList());

        alertRepository.saveAll(alerts);
    }

    public List<Alert> getAllTeacherAlerts(Teacher teacher) {
        return alertRepository.findByTeacher(teacher);
    }

    public List<Alert> getAllStudentAlerts(Student student) {
        return alertRepository.findByStudent(student);
    }
}
