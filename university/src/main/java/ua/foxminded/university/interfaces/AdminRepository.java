package ua.foxminded.university.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.foxminded.university.entities.Admin;

public interface AdminRepository extends JpaRepository<Admin, Integer> {

}
