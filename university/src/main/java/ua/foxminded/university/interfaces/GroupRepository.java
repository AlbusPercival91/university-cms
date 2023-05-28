package ua.foxminded.university.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.foxminded.university.entities.Group;

@Repository
public interface GroupRepository extends JpaRepository<Group, Integer> {

}
