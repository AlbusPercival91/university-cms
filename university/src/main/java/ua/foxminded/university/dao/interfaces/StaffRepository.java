package ua.foxminded.university.dao.interfaces;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.foxminded.university.dao.entities.Staff;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Integer> {

	List<Staff> findStaffByFirstNameAndLastName(String firstName, String lastName);

	List<Staff> findStaffByPosition(String position);
}
