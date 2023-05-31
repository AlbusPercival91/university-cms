package ua.foxminded.university.dao.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import javax.transaction.Transactional;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ua.foxminded.university.dao.entities.Teacher;
import ua.foxminded.university.dao.interfaces.TeacherRepository;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class TeacherService {
	private final TeacherRepository teacherRepository;

	public int createTeacher(Teacher teacher) {
		Teacher newTeacher = teacherRepository.save(teacher);
		log.info("Created teacher with id: {}", newTeacher.getId());
		return newTeacher.getId();
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

	public Teacher updateTeacherById(int teacherId, Teacher teacher) {
		Teacher existingTeacher = teacherRepository.findById(teacherId).orElseThrow(() -> {
			log.warn("Teacher with id {} not found", teacherId);
			return new NoSuchElementException("Teacher not found");
		});
		BeanUtils.copyProperties(teacher, existingTeacher, "id");
		return teacherRepository.save(existingTeacher);
	}

	public List<Teacher> getAllTeachers() {
		return teacherRepository.findAll();
	}
}
