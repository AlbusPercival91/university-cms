package ua.foxminded.university.security;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import ua.foxminded.university.dao.entities.User;

@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public UserDetails loadUserByUsername(String email) {
        List<String> entityNames = Arrays.asList("Student", "Teacher", "Staff", "Admin");

        for (String entityName : entityNames) {
            String nativeQuery = String.format("SELECT u FROM %s u WHERE u.email = :email", entityName);

            try {
                User user = entityManager.createQuery(nativeQuery, User.class).setParameter("email", email)
                        .getSingleResult();
                String role = entityName.toUpperCase();
                log.info(buildUserDetails(user, role).toString());
                return buildUserDetails(user, role);
            } catch (NoResultException ignored) {
                // Entity not found for this email, continue to the next entity
            }
        }
        throw new UsernameNotFoundException("User not found");
    }

    private UserDetails buildUserDetails(User user, String role) {
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getHashedPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role)));
    }

}
