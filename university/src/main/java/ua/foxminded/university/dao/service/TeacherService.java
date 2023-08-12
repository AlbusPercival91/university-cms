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
import ua.foxminded.university.dao.entities.Teacher;
import ua.foxminded.university.dao.interfaces.CourseRepository;
import ua.foxminded.university.dao.interfaces.TeacherRepository;
import ua.foxminded.university.dao.validation.UniqueEmailValidator;

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
			Teacher newTeacher = teacherRepository.save(teacher);
			addTeacherToTheCourse(newTeacher.getId(), course.getCourseName());
			log.info("Created teacher with id: {}", newTeacher.getId());
			return newTeacher.getId();
		}
		log.warn("Email already registered");
		throw new IllegalStateException("Email already registered");
	}

	public int deleteTeacherById(int teacherId) {
		Optional<Teacher> optionalTeacher = teacherRepository.findById(teacherId);

		if (optionalTeacher.isPresent()) {
			teacherRepository.deleteById(teacherId);
			log.info("Deleted teacher with id: {}", teacherId);
			return teacherId;
		} else {
			log.warn("Teacher with id {} not found", teacherId);
			throw new NoSuchElementException("Teacher not found");
		}
	}

	public Teacher updateTeacherById(int teacherId, Teacher targetTeacher) {
		Teacher existingTeacher = teacherRepository.findById(teacherId).orElseThrow(() -> {
			log.warn("Teacher with id {} not found", teacherId);
			return new NoSuchElementException("Teacher not found");
		});
		BeanUtils.copyProperties(targetTeacher, existingTeacher, "id");
		return teacherRepository.save(existingTeacher);
	}

	public int addTeacherToTheCourse(int teacherId, String courseName) {
		Teacher existingTeacher = teacherRepository.findById(teacherId).orElseThrow(() -> {
			log.warn("Teacher with id {} not found", teacherId);
			return new NoSuchElementException("Teacher not found");
		});
		Course existingCourse = courseRepository.findByCourseName(courseName).orElseThrow(() -> {
			log.warn("Course with name {} not found", courseName);
			return new NoSuchElementException("Course not found");
		});

		if (existingTeacher.getAssignedCourses().contains(existingCourse)) {
			throw new IllegalStateException("Teacher already assigned with this Course!");
		}
		return teacherRepository.addTeacherToTheCourse(existingTeacher.getId(), existingCourse.getCourseName());
	}

	public int removeTeacherFromCourse(int teacherId, String courseName) {
		Teacher existingTeacher = teacherRepository.findById(teacherId).orElseThrow(() -> {
			log.warn("Teacher with id {} not found", teacherId);
			return new NoSuchElementException("Teacher not found");
		});
		Course existingCourse = courseRepository.findByCourseName(courseName).orElseThrow(() -> {
			log.warn("Course with name {} not found", courseName);
			return new NoSuchElementException("Course not found");
		});

		if (!findTeachersRelatedToCourse(courseName).contains(existingTeacher)) {
			throw new IllegalStateException("Teacher is not related with this Course!");
		}
		return teacherRepository.removeTeacherFromCourse(teacherId, existingCourse.getCourseName());
	}

	public List<Teacher> getAllTeachers() {
		return teacherRepository.findAll();
	}

	public Optional<Teacher> findTeacherById(int teacherId) {
		return teacherRepository.findById(teacherId);
	}

	public Optional<Teacher> findTeacherByName(String firstName, String lastName) {
		return teacherRepository.findTeacherByFirstNameAndLastName(firstName, lastName);
	}

	public List<Teacher> findTeachersRelatedToCourse(String courseName) {
		return teacherRepository.findTeachersRelatedToCourse(courseName);
	}

	public List<Teacher> findAllByFacultyName(String facultyName) {
		return teacherRepository.findAllByDepartmentFacultyFacultyName(facultyName);
	}

	public List<Teacher> findAllByDepartmentIdAndDepartmentFacultyId(int departmentId, int facultyId) {
		return teacherRepository.findAllByDepartmentIdAndDepartmentFacultyId(departmentId, facultyId);
	}
}
