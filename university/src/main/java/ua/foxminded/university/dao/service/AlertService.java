package ua.foxminded.university.dao.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ua.foxminded.university.dao.entities.Teacher;
import ua.foxminded.university.dao.entities.Admin;
import ua.foxminded.university.dao.entities.Alert;
import ua.foxminded.university.dao.entities.Course;
import ua.foxminded.university.dao.entities.Department;
import ua.foxminded.university.dao.entities.Faculty;
import ua.foxminded.university.dao.entities.Group;
import ua.foxminded.university.dao.entities.Staff;
import ua.foxminded.university.dao.entities.Student;
import ua.foxminded.university.dao.interfaces.AdminRepository;
import ua.foxminded.university.dao.interfaces.AlertRepository;
import ua.foxminded.university.dao.interfaces.CourseRepository;
import ua.foxminded.university.dao.interfaces.DepartmentRepository;
import ua.foxminded.university.dao.interfaces.FacultyRepository;
import ua.foxminded.university.dao.interfaces.GroupRepository;
import ua.foxminded.university.dao.interfaces.StaffRepository;
import ua.foxminded.university.dao.interfaces.StudentRepository;
import ua.foxminded.university.dao.interfaces.TeacherRepository;
import ua.foxminded.university.validation.Message;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class AlertService {
    private final AlertRepository alertRepository;
    private final GroupRepository groupRepository;
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final StaffRepository staffRepository;
    private final AdminRepository adminRepository;
    private final FacultyRepository facultyRepository;
    private final DepartmentRepository departmentRepository;

    public void createTeacherAlert(LocalDateTime timestamp, String sender, int teacherId, String message) {
        Optional<Teacher> optionalTeacher = teacherRepository.findById(teacherId);

        if (optionalTeacher.isEmpty()) {
            throw new NoSuchElementException(Message.TEACHER_NOT_FOUND);
        }
        Alert alert = new Alert(timestamp, sender, optionalTeacher.get(), message);
        alertRepository.save(alert);
    }

    public void createStudentAlert(LocalDateTime timestamp, String sender, int studentId, String message) {
        Optional<Student> optionalStudent = studentRepository.findById(studentId);

        if (optionalStudent.isEmpty()) {
            throw new NoSuchElementException(Message.STUDENT_NOT_FOUND);
        }
        Alert alert = new Alert(timestamp, sender, optionalStudent.get(), message);
        alertRepository.save(alert);
    }

    public void createStaffAlert(LocalDateTime timestamp, String sender, int staffId, String message) {
        Optional<Staff> optionalStaff = staffRepository.findById(staffId);

        if (optionalStaff.isEmpty()) {
            throw new NoSuchElementException(Message.STAFF_NOT_FOUND);
        }
        Alert alert = new Alert(timestamp, sender, optionalStaff.get(), message);
        alertRepository.save(alert);
    }

    public void createAdminAlert(LocalDateTime timestamp, String sender, int adminId, String message) {
        Optional<Admin> optionalAdmin = adminRepository.findById(adminId);

        if (optionalAdmin.isEmpty()) {
            throw new NoSuchElementException(Message.ADMIN_NOT_FOUND);
        }
        Alert alert = new Alert(timestamp, sender, optionalAdmin.get(), message);
        alertRepository.save(alert);
    }

    public void createGroupAlert(LocalDateTime timestamp, String sender, int groupId, String message) {
        Optional<Group> optionalGroup = groupRepository.findById(groupId);

        if (optionalGroup.isEmpty()) {
            throw new NoSuchElementException(Message.GROUP_NOT_FOUND);
        }
        List<Student> students = studentRepository
                .findAllByGroupGroupNameOrderByIdAsc(optionalGroup.get().getGroupName());
        students.forEach(student -> createStudentAlert(timestamp, sender, student.getId(), message));
    }

    public void createCourseAlert(LocalDateTime timestamp, String sender, int courseId, String message) {
        Optional<Course> optionalCourse = courseRepository.findById(courseId);

        if (optionalCourse.isEmpty()) {
            throw new NoSuchElementException(Message.COURSE_NOT_FOUND);
        }
        List<Student> students = studentRepository
                .findStudentsRelatedToCourseOrderByIdAsc(optionalCourse.get().getCourseName());
        students.forEach(student -> createStudentAlert(timestamp, sender, student.getId(), message));

        List<Teacher> teachers = teacherRepository
                .findTeachersRelatedToCourseOrderByIdAsc(optionalCourse.get().getCourseName());
        teachers.forEach(teacher -> createTeacherAlert(timestamp, sender, teacher.getId(), message));
    }

    public void createFacultyAlert(LocalDateTime timestamp, String sender, int facultyId, String message) {
        Optional<Faculty> optionalFaculty = facultyRepository.findById(facultyId);

        if (optionalFaculty.isEmpty()) {
            throw new NoSuchElementException(Message.FACULTY_NOT_FOUND);
        }
        List<Student> students = studentRepository
                .findAllByGroupFacultyFacultyNameOrderByIdAsc(optionalFaculty.get().getFacultyName());
        students.forEach(student -> createStudentAlert(timestamp, sender, student.getId(), message));

        List<Teacher> teachers = teacherRepository
                .findAllByDepartmentFacultyFacultyNameOrderByIdAsc(optionalFaculty.get().getFacultyName());
        teachers.forEach(teacher -> createTeacherAlert(timestamp, sender, teacher.getId(), message));
    }

    public void createDepartmentAlert(LocalDateTime timestamp, String sender, int departmentId, String message) {
        Optional<Department> optionalDepartment = departmentRepository.findById(departmentId);

        if (optionalDepartment.isEmpty()) {
            throw new NoSuchElementException(Message.DEPARTMENT_NOT_FOUND);
        }
        List<Teacher> teachers = teacherRepository.findAllByDepartmentIdAndDepartmentFacultyIdOrderByIdAsc(
                optionalDepartment.get().getId(), optionalDepartment.get().getFaculty().getId());
        teachers.forEach(teacher -> createTeacherAlert(timestamp, sender, teacher.getId(), message));
    }

    private void createAlertsForStudents(LocalDateTime timestamp, String sender, String message) {
        List<Student> students = studentRepository.findAll();
        students.forEach(student -> createStudentAlert(timestamp, sender, student.getId(), message));
    }

    private void createAlertsForTeachers(LocalDateTime timestamp, String sender, String message) {
        List<Teacher> teachers = teacherRepository.findAll();
        teachers.forEach(teacher -> createTeacherAlert(timestamp, sender, teacher.getId(), message));
    }

    private void createAlertsForStaff(LocalDateTime timestamp, String sender, String message) {
        List<Staff> staffMembers = staffRepository.findAll();
        staffMembers.forEach(staff -> createStaffAlert(timestamp, sender, staff.getId(), message));
    }

    private void createAlertsForAdmins(LocalDateTime timestamp, String sender, String message) {
        List<Admin> admins = adminRepository.findAll();
        admins.forEach(admin -> createAdminAlert(timestamp, sender, admin.getId(), message));
    }

    public void createBroadcastAlert(LocalDateTime timestamp, String sender, String message) {
        createAlertsForStudents(timestamp, sender, message);
        createAlertsForTeachers(timestamp, sender, message);
        createAlertsForStaff(timestamp, sender, message);
        createAlertsForAdmins(timestamp, sender, message);
    }

    public int deleteAlertById(int alertId) {
        Optional<Alert> optionalAlert = alertRepository.findById(alertId);

        if (!optionalAlert.isPresent()) {
            log.warn(Message.ALERT_NOT_FOUND);
            throw new NoSuchElementException(Message.ALERT_NOT_FOUND);
        }
        alertRepository.deleteById(alertId);
        log.info(Message.DELETE_SUCCESS);
        return alertId;
    }

    public void toggleRead(int alertId) {
        Optional<Alert> optionalAlert = alertRepository.findById(alertId);

        if (optionalAlert.isPresent()) {
            Alert alert = optionalAlert.get();
            alert.setRead(!alert.isRead());
            alertRepository.save(alert);
        }
    }

    public List<Alert> getAllTeacherAlerts(Teacher teacher) {
        return alertRepository.findByTeacherOrderByTimestampDesc(teacher);
    }

    public List<Alert> getAllStudentAlerts(Student student) {
        return alertRepository.findByStudentOrderByTimestampDesc(student);
    }

    public List<Alert> getAllAdminAlerts(Admin admin) {
        return alertRepository.findByAdminOrderByTimestampDesc(admin);
    }

    public List<Alert> getAllStaffAlerts(Staff staff) {
        return alertRepository.findByStaffOrderByTimestampDesc(staff);
    }

    public List<Alert> findByTeacherAndDateBetween(int teacherId, LocalDateTime from, LocalDateTime to) {
        return alertRepository.findByTeacherIdAndTimestampBetweenOrderByTimestampDesc(teacherId, from, to);
    }

    public List<Alert> findByStudentAndDateBetween(int studentId, LocalDateTime from, LocalDateTime to) {
        return alertRepository.findByStudentIdAndTimestampBetweenOrderByTimestampDesc(studentId, from, to);
    }

    public List<Alert> findByAdminAndDateBetween(int adminId, LocalDateTime from, LocalDateTime to) {
        return alertRepository.findByAdminIdAndTimestampBetweenOrderByTimestampDesc(adminId, from, to);
    }

    public List<Alert> findByStaffAndDateBetween(int staffId, LocalDateTime from, LocalDateTime to) {
        return alertRepository.findByStaffIdAndTimestampBetweenOrderByTimestampDesc(staffId, from, to);
    }
}
