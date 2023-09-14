package ua.foxminded.university.dao.service;

import java.util.Arrays;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import ua.foxminded.university.dao.entities.User;
import ua.foxminded.university.dao.interfaces.UserRepository;

@RequiredArgsConstructor
@Service
@Transactional
public class UserService implements UserRepository {

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    public User getUserByUsername(String email) {
        List<String> entityNames = Arrays.asList("Student", "Teacher", "Staff", "Admin");

        for (String entityName : entityNames) {
            String nativeQuery = String.format("SELECT u FROM %s u WHERE u.email = :email", entityName);

            try {
                return entityManager.createQuery(nativeQuery, User.class).setParameter("email", email)
                        .getSingleResult();
            } catch (NoResultException ignored) {
                // Entity not found for this email, continue to the next entity
            }
        }
        throw new UsernameNotFoundException("User not found");
    }

}
