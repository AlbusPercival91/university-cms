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
			log.warn("Course already exists");
			throw new IllegalStateException("Course already exists");
		}
		Course newCourse = courseRepository.save(course);
		log.info("Created course with id: {}", newCourse.getId());
		return newCourse.getId();
	}

	public int deleteCourseById(int courseId) {
		Optional<Course> optionalCourse = courseRepository.findById(courseId);

		if (optionalCourse.isPresent()) {
			courseRepository.deleteById(courseId);
			log.info("Deleted course with id: {}", courseId);
			return courseId;
		} else {
			log.warn("Course with id {} not found", courseId);
			throw new NoSuchElementException("Course not found");
		}
	}

	public Course updateCourseById(int courseId, Course targetCourse) {
		Course existingCourse = courseRepository.findById(courseId).orElseThrow(() -> {
			log.warn("Course with id {} not found", courseId);
			return new NoSuchElementException("Course not found");
		});
		BeanUtils.copyProperties(targetCourse, existingCourse, "id");
		return courseRepository.save(existingCourse);
	}

	public List<Course> getAllCourses() {
		return courseRepository.findAll();
	}

	public List<Course> findCoursesRelatedToTeacher(int teacherId) {
		Teacher existingTeacher = teacherRepository.findById(teacherId).orElseThrow(() -> {
			log.warn("Teacher with id {} not found", teacherId);
			return new NoSuchElementException("Teacher not found");
		});
		return courseRepository.findCoursesRelatedToTeacher(existingTeacher.getId());
	}

	public List<Course> findCoursesRelatedToStudent(int studentId) {
		Student existingStudent = studentRepository.findById(studentId).orElseThrow(() -> {
			log.warn("Student with id {} not found", studentId);
			return new NoSuchElementException("Student not found");
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
