package ua.foxminded.university.dao.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ua.foxminded.university.dao.entities.Faculty;
import ua.foxminded.university.dao.interfaces.FacultyRepository;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class FacultyService {
	private final FacultyRepository facultyRepository;

	public int createFaculty(Faculty faculty) {
		Faculty newFaculty = facultyRepository.save(faculty);
		log.info("Created faculty with id: {}", newFaculty.getId());
		return newFaculty.getId();
	}

	public int deleteFacultyById(int facultyId) {
		Optional<Faculty> optionalFaculty = facultyRepository.findById(facultyId);

		if (optionalFaculty.isPresent()) {
			facultyRepository.deleteById(facultyId);
			log.info("Deleted faculty with id: {}", facultyId);
			return facultyId;
		} else {
			log.warn("Faculty with id {} not found", facultyId);
			throw new NoSuchElementException("Faculty not found");
		}
	}

	public Faculty updateFacultyById(int facultyId, Faculty targetFaculty) {
		Faculty existingFaculty = facultyRepository.findById(facultyId).orElseThrow(() -> {
			log.warn("Faculty with id {} not found", facultyId);
			return new NoSuchElementException("Faculty not found");
		});
		BeanUtils.copyProperties(targetFaculty, existingFaculty, "id");
		return facultyRepository.save(existingFaculty);
	}

	public List<Faculty> getAllFaculties() {
		return facultyRepository.findAll();
	}
}
