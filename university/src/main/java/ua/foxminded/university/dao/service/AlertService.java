package ua.foxminded.university.dao.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import ua.foxminded.university.dao.entities.Teacher;
import ua.foxminded.university.dao.entities.Alert;
import ua.foxminded.university.dao.entities.Group;
import ua.foxminded.university.dao.entities.Student;
import ua.foxminded.university.dao.interfaces.AlertRepository;
import ua.foxminded.university.dao.interfaces.GroupRepository;
import ua.foxminded.university.dao.interfaces.StudentRepository;
import ua.foxminded.university.validation.Message;

@RequiredArgsConstructor
@Service
@Transactional
public class AlertService {
    private final AlertRepository alertRepository;
    private final GroupRepository groupRepository;
    private final StudentRepository studentRepository;

    public void createTeacherAlert(LocalDate date, LocalTime time, Teacher teacher, String message) {
        Alert alert = new Alert(date, time, teacher, message);
        alertRepository.save(alert);
    }

    public void createStudentAlert(LocalDate date, LocalTime time, Student student, String message) {
        Alert alert = new Alert(date, time, student, message);
        alertRepository.save(alert);
    }

    public void createGroupAlert(LocalDate date, LocalTime time, int groupId, String message) {
        Optional<Group> optionalGroup = groupRepository.findById(groupId);

        if (optionalGroup.isEmpty()) {
            throw new NoSuchElementException(Message.GROUP_NOT_FOUND);
        }
        List<Student> students = studentRepository.findAllByGroupGroupName(optionalGroup.get().getGroupName());
        students.forEach(student -> createStudentAlert(date, time, student, message));
    }

    public List<Alert> getAllTeacherAlerts(Teacher teacher) {
        return alertRepository.findByTeacher(teacher);
    }

    public List<Alert> getAllStudentAlerts(Student student) {
        return alertRepository.findByStudent(student);
    }
}
