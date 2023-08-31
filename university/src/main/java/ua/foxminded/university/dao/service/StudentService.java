package ua.foxminded.university.dao.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ua.foxminded.university.dao.entities.Course;
import ua.foxminded.university.dao.entities.Student;
import ua.foxminded.university.dao.interfaces.CourseRepository;
import ua.foxminded.university.dao.interfaces.StudentRepository;
import ua.foxminded.university.security.UserRole;
import ua.foxminded.university.validation.Message;
import ua.foxminded.university.validation.UniqueEmailValidator;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class StudentService {
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final GroupService groupService;
    private final UniqueEmailValidator emailValidator;

    public int createStudent(Student student) {
        if (emailValidator.isValid(student)) {
            if (groupService.getAllGroups().contains(student.getGroup())) {
                student.setRole(UserRole.STUDENT);
                Student newStudent = studentRepository.save(student);
                log.info(Message.CREATE_SUCCESS);
                return newStudent.getId();
            } else {
                throw new NoSuchElementException(Message.GROUP_NOT_FOUND);
            }
        }
        log.warn(Message.EMAIL_EXISTS);
        throw new IllegalStateException(Message.EMAIL_EXISTS);
    }

    public int deleteStudentById(int studentId) {
        Optional<Student> optionalStudent = studentRepository.findById(studentId);

        if (optionalStudent.isPresent()) {
            studentRepository.deleteById(studentId);
            log.info(Message.DELETE_SUCCESS);
            return studentId;
        } else {
            log.warn(Message.STUDENT_NOT_FOUND);
            throw new NoSuchElementException(Message.STUDENT_NOT_FOUND);
        }
    }

    public Student updateStudentById(int studentId, Student targetStudent) {
        Student existingStudent = studentRepository.findById(studentId).orElseThrow(() -> {
            log.warn(Message.STUDENT_NOT_FOUND);
            return new NoSuchElementException(Message.STUDENT_NOT_FOUND);
        });

        if (!emailValidator.isValid(targetStudent)
                && !findStudentByEmail(existingStudent.getEmail()).get().getEmail().equals(targetStudent.getEmail())) {
            log.warn(Message.EMAIL_EXISTS);
            throw new IllegalStateException(Message.EMAIL_EXISTS);
        }
        BeanUtils.copyProperties(targetStudent, existingStudent, "id", "courses", "hashedPassword", "role");
        return studentRepository.save(existingStudent);
    }

    public Student changeStudentPasswordById(int studentId, String oldPassword, String newPassword) {
        Student existingStudent = studentRepository.findById(studentId).orElseThrow(() -> {
            log.warn(Message.STUDENT_NOT_FOUND);
            return new NoSuchElementException(Message.STUDENT_NOT_FOUND);
        });

        if (!existingStudent.isPasswordValid(oldPassword)) {
            log.warn(Message.PASSWORD_WRONG);
            throw new IllegalStateException(Message.PASSWORD_WRONG);
        }
        existingStudent.setPassword(newPassword);
        return studentRepository.save(existingStudent);
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public int addStudentToTheCourse(int studentId, String courseName) {
        Student existingStudent = studentRepository.findById(studentId).orElseThrow(() -> {
            log.warn(Message.STUDENT_NOT_FOUND);
            return new NoSuchElementException(Message.STUDENT_NOT_FOUND);
        });
        Course existingCourse = courseRepository.findByCourseName(courseName).orElseThrow(() -> {
            log.warn(Message.COURSE_NOT_FOUND);
            return new NoSuchElementException(Message.COURSE_NOT_FOUND);
        });

        if (existingStudent.getCourses().contains(existingCourse)) {
            throw new IllegalStateException(Message.USER_ALREADY_ASSIGNED);
        }
        return studentRepository.addStudentToTheCourse(existingStudent.getId(), existingCourse.getCourseName());
    }

    public int removeStudentFromCourse(int studentId, String courseName) {
        Student existingStudent = studentRepository.findById(studentId).orElseThrow(() -> {
            log.warn(Message.STUDENT_NOT_FOUND);
            return new NoSuchElementException(Message.STUDENT_NOT_FOUND);
        });
        Course existingCourse = courseRepository.findByCourseName(courseName).orElseThrow(() -> {
            log.warn(Message.COURSE_NOT_FOUND);
            return new NoSuchElementException(Message.COURSE_NOT_FOUND);
        });

        if (!findStudentsRelatedToCourse(courseName).contains(existingStudent)) {
            throw new IllegalStateException(Message.USER_NOT_RELATED_WITH_COURSE);
        }
        return studentRepository.removeStudentFromCourse(studentId, existingCourse.getCourseName());
    }

    public Optional<Student> findStudentById(int studentId) {
        return studentRepository.findById(studentId);
    }

    public List<Student> findStudentsRelatedToCourse(String courseName) {
        return studentRepository.findStudentsRelatedToCourse(courseName);
    }

    public List<Student> findAllByGroupName(String groupName) {
        return studentRepository.findAllByGroupGroupName(groupName);
    }

    public List<Student> findAllByGroupFacultyFacultyName(String facultyName) {
        return studentRepository.findAllByGroupFacultyFacultyName(facultyName);
    }

    public List<Student> findStudentByName(String firstName, String lastName) {
        return studentRepository.findStudentByFirstNameAndLastName(firstName, lastName);
    }

    public Optional<Student> findStudentByEmail(String email) {
        return studentRepository.findByEmail(email);
    }

}
