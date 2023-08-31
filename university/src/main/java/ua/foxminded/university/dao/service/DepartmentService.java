package ua.foxminded.university.dao.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ua.foxminded.university.dao.entities.Department;
import ua.foxminded.university.dao.entities.Faculty;
import ua.foxminded.university.dao.interfaces.DepartmentRepository;
import ua.foxminded.university.dao.interfaces.FacultyRepository;
import ua.foxminded.university.validation.Message;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class DepartmentService {
    private final DepartmentRepository departmentRepository;
    private final FacultyRepository facultyRepository;

    public int createDepartment(Department department) {
        String facultyName = department.getFaculty().getFacultyName();
        Optional<Faculty> faculty = facultyRepository.findFacultyByFacultyName(facultyName);

        if (faculty.get().getDepartments().stream().anyMatch(d -> d.getName().equals(department.getName()))) {
            log.warn(Message.DEPARTMENT_EXISTS);
            throw new IllegalStateException(Message.DEPARTMENT_EXISTS);
        }

        Department newDepartment = departmentRepository.save(department);
        log.info(Message.CREATE_SUCCESS);
        return newDepartment.getId();
    }

    public int deleteDepartmentById(int departmentId) {
        Optional<Department> optionalDepartment = departmentRepository.findById(departmentId);

        if (optionalDepartment.isPresent()) {
            departmentRepository.deleteById(departmentId);
            log.info(Message.DELETE_SUCCESS);
            return departmentId;
        } else {
            log.warn(Message.DEPARTMENT_NOT_FOUND);
            throw new NoSuchElementException(Message.DEPARTMENT_NOT_FOUND);
        }
    }

    public Department updateDepartmentById(int departmentId, Department targetDepartment) {
        Department existingDepartment = departmentRepository.findById(departmentId).orElseThrow(() -> {
            log.warn(Message.DEPARTMENT_NOT_FOUND);
            return new NoSuchElementException(Message.DEPARTMENT_NOT_FOUND);
        });

        if (targetDepartment.getFaculty().getDepartments().stream()
                .anyMatch(d -> d.getName().equals(targetDepartment.getName()))) {
            log.warn(Message.DEPARTMENT_EXISTS);
            throw new IllegalStateException(Message.DEPARTMENT_EXISTS);
        }
        BeanUtils.copyProperties(targetDepartment, existingDepartment, "id");
        return departmentRepository.save(existingDepartment);
    }

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    public Optional<Department> findDepartmentById(int departmentId) {
        return departmentRepository.findById(departmentId);
    }

    public List<Department> findAllByFacultyName(String facultyName) {
        return departmentRepository.findAllByFacultyFacultyName(facultyName);
    }

    public List<Department> findDepartmentByName(String departmentName) {
        return departmentRepository.findDepartmentByName(departmentName);
    }
}
