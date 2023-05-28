package ua.foxminded.university.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.foxminded.university.entities.TimeTable;

public interface TimeTableRepository extends JpaRepository<TimeTable, Integer> {

}
