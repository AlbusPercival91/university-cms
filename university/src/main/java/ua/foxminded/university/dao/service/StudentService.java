package ua.foxminded.university.dao.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ua.foxminded.university.dao.entities.Student;
import ua.foxminded.university.dao.interfaces.StudentRepository;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class StudentService {
	private final StudentRepository studentRepository;

	public int createStudent(Student student) {
		Student newStudent = studentRepository.save(student);
		log.info("Created student with id: {}", newStudent.getId());
		return newStudent.getId();
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

	public Student updateStudentById(int studentId, Student student) {
		Student existingStudent = studentRepository.findById(studentId).orElseThrow(() -> {
			log.warn("Student with id {} not found", studentId);
			return new NoSuchElementException("Student not found");
		});

		existingStudent.setFirstName(student.getFirstName());
		existingStudent.setLastName(student.getLastName());
		return studentRepository.save(existingStudent);
	}

	public List<Student> getAllStudents() {
		return studentRepository.findAll();
	}
}
