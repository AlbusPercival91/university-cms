package ua.foxminded.university.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.foxminded.university.entities.Staff;

public interface StaffRepository extends JpaRepository<Staff, Integer> {

}
