package ua.foxminded.university.dao.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ua.foxminded.university.dao.entities.Admin;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Integer> {

}
