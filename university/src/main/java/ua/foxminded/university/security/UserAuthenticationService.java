package ua.foxminded.university.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ua.foxminded.university.dao.entities.User;
import ua.foxminded.university.dao.service.UserService;

@Service
public class UserAuthenticationService {

    @Autowired
    private UserService userService;

    public String getAuthenticatedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    public String getAuthenticatedUserNameAndRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userService.getUserByUsername(email);
        return user.getFirstName() + " " + user.getLastName() + " (" + user.getRole() + ")";
    }

}
