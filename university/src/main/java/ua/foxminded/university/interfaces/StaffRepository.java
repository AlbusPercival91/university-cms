package ua.foxminded.university.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.foxminded.university.entities.Staff;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Integer> {

}
