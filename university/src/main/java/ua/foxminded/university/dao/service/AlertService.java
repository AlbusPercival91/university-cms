package ua.foxminded.university.dao.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import ua.foxminded.university.dao.entities.Teacher;
import ua.foxminded.university.dao.entities.Alert;
import ua.foxminded.university.dao.entities.Department;
import ua.foxminded.university.dao.entities.Faculty;
import ua.foxminded.university.dao.entities.Group;
import ua.foxminded.university.dao.entities.Student;
import ua.foxminded.university.dao.interfaces.AlertRepository;
import ua.foxminded.university.dao.interfaces.DepartmentRepository;
import ua.foxminded.university.dao.interfaces.GroupRepository;
import ua.foxminded.university.dao.interfaces.StudentRepository;
import ua.foxminded.university.dao.interfaces.TeacherRepository;

@RequiredArgsConstructor
@Service
@Transactional
public class AlertService {
    private final AlertRepository alertRepository;
    private final GroupRepository groupRepository;
    private final DepartmentRepository departmentRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    public int createTeacherAlert(LocalDate date, LocalTime time, Teacher teacher, String message) {
        Alert alert = new Alert(date, time, teacher, message);
        alertRepository.save(alert);
        return alert.getId();
    }

    public int createStudentAlert(LocalDate date, LocalTime time, Student student, String message) {
        Alert alert = new Alert(date, time, student, message);
        alertRepository.save(alert);
        return alert.getId();
    }

    public int createGroupAlert(LocalDate date, LocalTime time, Group group, String message) {
        int alertId = 0;
//        List<Student> students = studentRepository.findAllByGroupFacultyFacultyName(faculty.getFacultyName());
//
//        for (Student student : students) {
//            alertId = createStudentAlert(date, time, student, message);
//        }
        return alertId;
    }

    public List<Alert> getAllTeacherAlerts(Teacher teacher) {
        return alertRepository.findByTeacher(teacher);
    }

    public List<Alert> getAllStudentAlerts(Student student) {
        return alertRepository.findByStudent(student);
    }
}
