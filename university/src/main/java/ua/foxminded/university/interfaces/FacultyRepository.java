package ua.foxminded.university.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.foxminded.university.entities.Faculty;

public interface FacultyRepository extends JpaRepository<Faculty, Integer> {

}
