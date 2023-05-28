package ua.foxminded.university.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.foxminded.university.entities.Group;

public interface GroupRepository extends JpaRepository<Group, Integer> {

}
