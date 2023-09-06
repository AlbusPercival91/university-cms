package ua.foxminded.university.dao.interfaces;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.foxminded.university.dao.entities.Staff;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Integer> {

    List<Staff> findStaffByFirstNameAndLastNameOrderByIdAsc(String firstName, String lastName);

    List<Staff> findStaffByPositionOrderByIdAsc(String position);

    Optional<Staff> findByEmail(String email);
}
