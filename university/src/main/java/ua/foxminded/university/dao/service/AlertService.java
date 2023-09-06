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

    public void createStaffAlert(LocalDateTime timestamp, int staffId, String message) {
        Optional<Staff> optionalStaff = staffRepository.findById(staffId);

        if (optionalStaff.isEmpty()) {
            throw new NoSuchElementException(Message.STAFF_NOT_FOUND);
        }
        Alert alert = new Alert(timestamp, optionalStaff.get(), message);
        alertRepository.save(alert);
    }

    public void createAdminAlert(LocalDateTime timestamp, int adminId, String message) {
        Optional<Admin> optionalAdmin = adminRepository.findById(adminId);

        if (optionalAdmin.isEmpty()) {
            throw new NoSuchElementException(Message.ADMIN_NOT_FOUND);
        }
        Alert alert = new Alert(timestamp, optionalAdmin.get(), message);
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

    public void createCourseAlert(LocalDateTime timestamp, int courseId, String message) {
        Optional<Course> optionalCourse = courseRepository.findById(courseId);

        if (optionalCourse.isEmpty()) {
            throw new NoSuchElementException(Message.COURSE_NOT_FOUND);
        }
        List<Student> students = studentRepository.findStudentsRelatedToCourse(optionalCourse.get().getCourseName());
        students.forEach(student -> createStudentAlert(timestamp, student.getId(), message));

        List<Teacher> teachers = teacherRepository.findTeachersRelatedToCourse(optionalCourse.get().getCourseName());
        teachers.forEach(teacher -> createTeacherAlert(timestamp, teacher.getId(), message));
    }

    public void createFacultyAlert(LocalDateTime timestamp, int facultyId, String message) {
        Optional<Faculty> optionalFaculty = facultyRepository.findById(facultyId);

        if (optionalFaculty.isEmpty()) {
            throw new NoSuchElementException(Message.FACULTY_NOT_FOUND);
        }
        List<Student> students = studentRepository
                .findAllByGroupFacultyFacultyName(optionalFaculty.get().getFacultyName());
        students.forEach(student -> createStudentAlert(timestamp, student.getId(), message));

        List<Teacher> teachers = teacherRepository
                .findAllByDepartmentFacultyFacultyName(optionalFaculty.get().getFacultyName());
        teachers.forEach(teacher -> createTeacherAlert(timestamp, teacher.getId(), message));
    }

    public void createDepartmentAlert(LocalDateTime timestamp, int departmentId, String message) {
        Optional<Department> optionalDepartment = departmentRepository.findById(departmentId);

        if (optionalDepartment.isEmpty()) {
            throw new NoSuchElementException(Message.DEPARTMENT_NOT_FOUND);
        }
        List<Teacher> teachers = teacherRepository.findAllByDepartmentIdAndDepartmentFacultyId(
                optionalDepartment.get().getId(), optionalDepartment.get().getFaculty().getId());
        teachers.forEach(teacher -> createTeacherAlert(timestamp, teacher.getId(), message));
    }

    public int deleteAlertById(int alertId) {
        Optional<Alert> optionalAlert = alertRepository.findById(alertId);

        if (optionalAlert.isPresent()) {
            alertRepository.deleteById(alertId);
            log.info(Message.DELETE_SUCCESS);
            return alertId;
        } else {
            log.warn(Message.ALERT_NOT_FOUND);
            throw new NoSuchElementException(Message.ALERT_NOT_FOUND);
        }
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
