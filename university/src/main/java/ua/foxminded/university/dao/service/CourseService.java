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
import ua.foxminded.university.dao.entities.Teacher;
import ua.foxminded.university.dao.interfaces.CourseRepository;
import ua.foxminded.university.dao.interfaces.StudentRepository;
import ua.foxminded.university.dao.interfaces.TeacherRepository;
import ua.foxminded.university.validation.Message;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class CourseService {
    private final CourseRepository courseRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;

    public int createCourse(Course course) {
        if (findByCourseName(course.getCourseName()).isPresent()) {
            log.warn(Message.RECORD_EXISTS);
            throw new IllegalStateException(Message.RECORD_EXISTS);
        }
        Course newCourse = courseRepository.save(course);
        log.info(Message.CREATE_SUCCESS);
        return newCourse.getId();
    }

    public int deleteCourseById(int courseId) {
        Optional<Course> optionalCourse = courseRepository.findById(courseId);

        if (optionalCourse.isPresent()) {
            courseRepository.deleteById(courseId);
            log.info(Message.DELETE_SUCCESS);
            return courseId;
        } else {
            log.warn(Message.COURSE_NOT_FOUND);
            throw new NoSuchElementException(Message.COURSE_NOT_FOUND);
        }
    }

    public Course updateCourseById(int courseId, Course targetCourse) {
        Course existingCourse = courseRepository.findById(courseId).orElseThrow(() -> {
            log.warn(Message.COURSE_NOT_FOUND);
            return new NoSuchElementException(Message.COURSE_NOT_FOUND);
        });
        BeanUtils.copyProperties(targetCourse, existingCourse, "id");
        return courseRepository.save(existingCourse);
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public List<Course> findCoursesRelatedToTeacher(int teacherId) {
        Teacher existingTeacher = teacherRepository.findById(teacherId).orElseThrow(() -> {
            log.warn(Message.TEACHER_NOT_FOUND);
            return new NoSuchElementException(Message.TEACHER_NOT_FOUND);
        });
        return courseRepository.findCoursesRelatedToTeacher(existingTeacher.getId());
    }

    public List<Course> findCoursesRelatedToStudent(int studentId) {
        Student existingStudent = studentRepository.findById(studentId).orElseThrow(() -> {
            log.warn(Message.STUDENT_NOT_FOUND);
            return new NoSuchElementException(Message.STUDENT_NOT_FOUND);
        });
        return courseRepository.findCoursesRelatedToStudent(existingStudent.getId());
    }

    public Optional<Course> findCourseById(int courseId) {
        return courseRepository.findById(courseId);
    }

    public Optional<Course> findByCourseName(String courseName) {
        return courseRepository.findByCourseName(courseName);
    }
}
