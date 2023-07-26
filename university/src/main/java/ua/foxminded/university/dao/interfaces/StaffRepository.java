package ua.foxminded.university.dao.interfaces;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.foxminded.university.dao.entities.Staff;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Integer> {

	Optional<Staff> findStaffByFirstNameAndLastName(String firstName, String lastName);
}
