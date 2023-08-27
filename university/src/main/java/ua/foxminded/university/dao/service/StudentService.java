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
				log.info("Created student with id: {}", newStudent.getId());
				return newStudent.getId();
			} else {
				throw new NoSuchElementException("Group not found");
			}
		}
		log.warn("Email already registered");
		throw new IllegalStateException("Email already registered");
	}

	public int deleteStudentById(int studentId) {
		Optional<Student> optionalStudent = studentRepository.findById(studentId);

		if (optionalStudent.isPresent()) {
			studentRepository.deleteById(studentId);
			log.info("Deleted student with id: {}", studentId);
			return studentId;
		} else {
			log.warn("Student with id {} not found", studentId);
			throw new NoSuchElementException("Student not found");
		}
	}

	public Student updateStudentById(int studentId, Student targetStudent) {
		Student existingStudent = studentRepository.findById(studentId).orElseThrow(() -> {
			log.warn("Student with id {} not found", studentId);
			return new NoSuchElementException("Student not found");
		});

		if (!emailValidator.isValid(targetStudent)
				&& !findStudentByEmail(existingStudent.getEmail()).get().getEmail().equals(targetStudent.getEmail())) {
			log.warn("Email already registered");
			throw new IllegalStateException("Email already registered");
		}
		BeanUtils.copyProperties(targetStudent, existingStudent, "id", "courses", "hashedPassword", "role");
		return studentRepository.save(existingStudent);
	}

	public Student changeStudentPasswordById(int studentId, String oldPassword, String newPassword) {
		Student existingStudent = studentRepository.findById(studentId).orElseThrow(() -> {
			log.warn("Student with id {} not found", studentId);
			return new NoSuchElementException("Student not found");
		});

		if (!existingStudent.isPasswordValid(oldPassword)) {
			log.warn("Password incorrect");
			throw new IllegalStateException("Password incorrect");
		}
		existingStudent.setPassword(newPassword);
		return studentRepository.save(existingStudent);
	}

	public List<Student> getAllStudents() {
		return studentRepository.findAll();
	}

	public int addStudentToTheCourse(int studentId, String courseName) {
		Student existingStudent = studentRepository.findById(studentId).orElseThrow(() -> {
			log.warn("Student with id {} not found", studentId);
			return new NoSuchElementException("Student not found");
		});
		Course existingCourse = courseRepository.findByCourseName(courseName).orElseThrow(() -> {
			log.warn("Course with name {} not found", courseName);
			return new NoSuchElementException("Course not found");
		});

		if (existingStudent.getCourses().contains(existingCourse)) {
			throw new IllegalStateException("Student already assigned with this Course!");
		}
		return studentRepository.addStudentToTheCourse(existingStudent.getId(), existingCourse.getCourseName());
	}

	public int removeStudentFromCourse(int studentId, String courseName) {
		Student existingStudent = studentRepository.findById(studentId).orElseThrow(() -> {
			log.warn("Student with id {} not found", studentId);
			return new NoSuchElementException("Student not found");
		});
		Course existingCourse = courseRepository.findByCourseName(courseName).orElseThrow(() -> {
			log.warn("Course with name {} not found", courseName);
			return new NoSuchElementException("Course not found");
		});

		if (!findStudentsRelatedToCourse(courseName).contains(existingStudent)) {
			throw new IllegalStateException("Student is not related with this Course!");
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
