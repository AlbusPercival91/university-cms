package ua.foxminded.university.dao.interfaces;

import org.springframework.stereotype.Repository;

import ua.foxminded.university.dao.entities.User;

@Repository
public interface UserRepository {

    User getUserByUsername(String email);
}
