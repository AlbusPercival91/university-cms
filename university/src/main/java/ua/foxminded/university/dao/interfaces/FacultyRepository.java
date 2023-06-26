package ua.foxminded.university.dao.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ua.foxminded.university.dao.entities.Faculty;

@Repository
public interface FacultyRepository extends JpaRepository<Faculty, Integer> {

}
