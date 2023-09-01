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
import ua.foxminded.university.validation.Message;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class FacultyService {
    private final FacultyRepository facultyRepository;

    public int createFaculty(Faculty faculty) {
        if (facultyRepository.findFacultyByFacultyName(faculty.getFacultyName()).isPresent()) {
            throw new IllegalStateException(Message.FACULTY_EXISTS);
        }

        Faculty newFaculty = facultyRepository.save(faculty);
        log.info(Message.CREATE_SUCCESS);
        return newFaculty.getId();
    }

    public int deleteFacultyById(int facultyId) {
        Optional<Faculty> optionalFaculty = facultyRepository.findById(facultyId);

        if (optionalFaculty.isPresent()) {
            facultyRepository.deleteById(facultyId);
            log.info(Message.DELETE_SUCCESS);
            return facultyId;
        } else {
            log.warn(Message.FACULTY_NOT_FOUND);
            throw new NoSuchElementException(Message.FACULTY_NOT_FOUND);
        }
    }

    public Faculty updateFacultyById(int facultyId, Faculty targetFaculty) {
        Faculty existingFaculty = facultyRepository.findById(facultyId).orElseThrow(() -> {
            log.warn(Message.FACULTY_NOT_FOUND);
            return new NoSuchElementException(Message.FACULTY_NOT_FOUND);
        });

        if (getAllFaculties().stream().anyMatch(f -> f.getFacultyName().equals(targetFaculty.getFacultyName()))) {
            log.warn(Message.FACULTY_EXISTS);
            throw new IllegalStateException(Message.FACULTY_EXISTS);
        }
        BeanUtils.copyProperties(targetFaculty, existingFaculty, "id");
        return facultyRepository.save(existingFaculty);
    }

    public List<Faculty> getAllFaculties() {
        return facultyRepository.findAll();
    }

    public Optional<Faculty> findFacultyById(int facultyId) {
        return facultyRepository.findById(facultyId);
    }

    public Optional<Faculty> findFacultyByFacultyName(String facultyName) {
        return facultyRepository.findFacultyByFacultyName(facultyName);
    }

}
