package ua.foxminded.university.dao.interfaces;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ua.foxminded.university.dao.entities.Faculty;
import ua.foxminded.university.dao.entities.Group;

@Repository
public interface GroupRepository extends JpaRepository<Group, Integer> {

	List<Group> findAllByFaculty(Faculty faculty);
}
