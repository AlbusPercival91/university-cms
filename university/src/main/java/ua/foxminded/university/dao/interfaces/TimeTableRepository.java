package ua.foxminded.university.dao.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ua.foxminded.university.dao.entities.TimeTable;

@Repository
public interface TimeTableRepository extends JpaRepository<TimeTable, Integer> {

}
