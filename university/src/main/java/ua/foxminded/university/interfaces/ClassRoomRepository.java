package ua.foxminded.university.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.foxminded.university.entities.ClassRoom;

public interface ClassRoomRepository extends JpaRepository<ClassRoom, Integer> {

}
