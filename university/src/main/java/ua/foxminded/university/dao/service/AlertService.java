package ua.foxminded.university.dao.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import ua.foxminded.university.dao.entities.Teacher;
import ua.foxminded.university.dao.entities.Admin;
import ua.foxminded.university.dao.entities.Alert;
import ua.foxminded.university.dao.entities.Group;
import ua.foxminded.university.dao.entities.Staff;
import ua.foxminded.university.dao.entities.Student;
import ua.foxminded.university.dao.interfaces.AlertRepository;
import ua.foxminded.university.dao.interfaces.GroupRepository;
import ua.foxminded.university.dao.interfaces.StudentRepository;
import ua.foxminded.university.dao.interfaces.TeacherRepository;
import ua.foxminded.university.validation.Message;

@RequiredArgsConstructor
@Service
@Transactional
public class AlertService {
    private final AlertRepository alertRepository;
    private final GroupRepository groupRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    public void createTeacherAlert(LocalDateTime timestamp, int teacherId, String message) {
        Optional<Teacher> optionalTeacher = teacherRepository.findById(teacherId);

        if (optionalTeacher.isEmpty()) {
            throw new NoSuchElementException(Message.TEACHER_NOT_FOUND);
        }
        Alert alert = new Alert(timestamp, optionalTeacher.get(), message);
        alertRepository.save(alert);
    }

    public void createStudentAlert(LocalDateTime timestamp, int studentId, String message) {
        Optional<Student> optionalStudent = studentRepository.findById(studentId);

        if (optionalStudent.isEmpty()) {
            throw new NoSuchElementException(Message.STUDENT_NOT_FOUND);
        }
        Alert alert = new Alert(timestamp, optionalStudent.get(), message);
        alertRepository.save(alert);
    }

    public void createGroupAlert(LocalDateTime timestamp, int groupId, String message) {
        Optional<Group> optionalGroup = groupRepository.findById(groupId);

        if (optionalGroup.isEmpty()) {
            throw new NoSuchElementException(Message.GROUP_NOT_FOUND);
        }
        List<Student> students = studentRepository.findAllByGroupGroupName(optionalGroup.get().getGroupName());
        students.forEach(student -> createStudentAlert(timestamp, student.getId(), message));
    }

    public List<Alert> getAllTeacherAlerts(Teacher teacher) {
        return alertRepository.findByTeacher(teacher);
    }

    public List<Alert> getAllStudentAlerts(Student student) {
        return alertRepository.findByStudent(student);
    }

    public List<Alert> getAllAdminAlerts(Admin admin) {
        return alertRepository.findByAdmin(admin);
    }

    public List<Alert> getAllStaffAlerts(Staff staff) {
        return alertRepository.findByStaff(staff);
    }
}
