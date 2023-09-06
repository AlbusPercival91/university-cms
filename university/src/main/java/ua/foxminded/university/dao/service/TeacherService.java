package ua.foxminded.university.dao.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ua.foxminded.university.dao.entities.Course;
import ua.foxminded.university.dao.entities.Teacher;
import ua.foxminded.university.dao.interfaces.CourseRepository;
import ua.foxminded.university.dao.interfaces.TeacherRepository;
import ua.foxminded.university.security.UserRole;
import ua.foxminded.university.validation.Message;
import ua.foxminded.university.validation.UniqueEmailValidator;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class TeacherService {
    private final TeacherRepository teacherRepository;
    private final CourseRepository courseRepository;
    private final UniqueEmailValidator emailValidator;

    public int createAndAssignTeacherToCourse(Teacher teacher, Course course) {
        if (emailValidator.isValid(teacher)) {
            teacher.setRole(UserRole.TEACHER);
            Teacher newTeacher = teacherRepository.save(teacher);
            addTeacherToTheCourse(newTeacher.getId(), course.getCourseName());
            log.info(Message.CREATE_SUCCESS);
            return newTeacher.getId();
        }
        log.warn(Message.EMAIL_EXISTS);
        throw new IllegalStateException(Message.EMAIL_EXISTS);
    }

    public int deleteTeacherById(int teacherId) {
        Optional<Teacher> optionalTeacher = teacherRepository.findById(teacherId);

        if (optionalTeacher.isPresent()) {
            teacherRepository.deleteById(teacherId);
            log.info(Message.DELETE_SUCCESS);
            return teacherId;
        } else {
            log.warn(Message.TEACHER_NOT_FOUND);
            throw new NoSuchElementException(Message.TEACHER_NOT_FOUND);
        }
    }

    public Teacher updateTeacherById(int teacherId, Teacher targetTeacher) {
        Teacher existingTeacher = teacherRepository.findById(teacherId).orElseThrow(() -> {
            log.warn(Message.TEACHER_NOT_FOUND);
            return new NoSuchElementException(Message.TEACHER_NOT_FOUND);
        });

        if (!emailValidator.isValid(targetTeacher)
                && !findTeacherByEmail(existingTeacher.getEmail()).get().getEmail().equals(targetTeacher.getEmail())) {
            log.warn(Message.EMAIL_EXISTS);
            throw new IllegalStateException(Message.EMAIL_EXISTS);
        }
        BeanUtils.copyProperties(targetTeacher, existingTeacher, "id", "assignedCourses", "hashedPassword", "role");
        return teacherRepository.save(existingTeacher);
    }

    public Teacher changeTeacherPasswordById(int teacherId, String oldPassword, String newPassword) {
        Teacher existingTeacher = teacherRepository.findById(teacherId).orElseThrow(() -> {
            log.warn(Message.TEACHER_NOT_FOUND);
            return new NoSuchElementException(Message.TEACHER_NOT_FOUND);
        });

        if (!existingTeacher.isPasswordValid(oldPassword)) {
            log.warn(Message.PASSWORD_WRONG);
            throw new IllegalStateException(Message.PASSWORD_WRONG);
        }
        existingTeacher.setPassword(newPassword);
        return teacherRepository.save(existingTeacher);
    }

    public int addTeacherToTheCourse(int teacherId, String courseName) {
        Teacher existingTeacher = teacherRepository.findById(teacherId).orElseThrow(() -> {
            log.warn(Message.TEACHER_NOT_FOUND);
            return new NoSuchElementException(Message.TEACHER_NOT_FOUND);
        });
        Course existingCourse = courseRepository.findByCourseName(courseName).orElseThrow(() -> {
            log.warn(Message.COURSE_NOT_FOUND);
            return new NoSuchElementException(Message.COURSE_NOT_FOUND);
        });

        if (existingTeacher.getAssignedCourses().contains(existingCourse)) {
            throw new IllegalStateException(Message.USER_ALREADY_ASSIGNED);
        }
        return teacherRepository.addTeacherToTheCourse(existingTeacher.getId(), existingCourse.getCourseName());
    }

    public int removeTeacherFromCourse(int teacherId, String courseName) {
        Teacher existingTeacher = teacherRepository.findById(teacherId).orElseThrow(() -> {
            log.warn(Message.TEACHER_NOT_FOUND);
            return new NoSuchElementException(Message.TEACHER_NOT_FOUND);
        });
        Course existingCourse = courseRepository.findByCourseName(courseName).orElseThrow(() -> {
            log.warn(Message.COURSE_NOT_FOUND);
            return new NoSuchElementException(Message.COURSE_NOT_FOUND);
        });

        if (!findTeachersRelatedToCourse(courseName).contains(existingTeacher)) {
            throw new IllegalStateException(Message.USER_NOT_RELATED_WITH_COURSE);
        }
        return teacherRepository.removeTeacherFromCourse(teacherId, existingCourse.getCourseName());
    }

    public List<Teacher> getAllTeachers() {
        return teacherRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    public Optional<Teacher> findTeacherById(int teacherId) {
        return teacherRepository.findById(teacherId);
    }

    public List<Teacher> findTeacherByName(String firstName, String lastName) {
        return teacherRepository.findTeacherByFirstNameAndLastNameOrderByIdAsc(firstName, lastName);
    }

    public List<Teacher> findTeachersRelatedToCourse(String courseName) {
        return teacherRepository.findTeachersRelatedToCourseOrderByIdAsc(courseName);
    }

    public List<Teacher> findAllByFacultyName(String facultyName) {
        return teacherRepository.findAllByDepartmentFacultyFacultyNameOrderByIdAsc(facultyName);
    }

    public List<Teacher> findAllByDepartmentIdAndDepartmentFacultyId(int departmentId, int facultyId) {
        return teacherRepository.findAllByDepartmentIdAndDepartmentFacultyIdOrderByIdAsc(departmentId, facultyId);
    }

    public Optional<Teacher> findTeacherByEmail(String email) {
        return teacherRepository.findByEmail(email);
    }
}
