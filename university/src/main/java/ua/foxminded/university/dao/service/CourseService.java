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
import ua.foxminded.university.dao.interfaces.CourseRepository;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class CourseService {
	private final CourseRepository courseRepository;

	public int createCourse(Course course) {
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

	public Course updateCourseById(int courseId, Course course) {
		Course existingCourse = courseRepository.findById(courseId).orElseThrow(() -> {
			log.warn("Course with id {} not found", courseId);
			return new NoSuchElementException("Course not found");
		});
		BeanUtils.copyProperties(course, existingCourse, "id");
		return courseRepository.save(existingCourse);
	}

	public List<Course> getAllCourses() {
		return courseRepository.findAll();
	}
}
