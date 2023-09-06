package ua.foxminded.university.dao.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ua.foxminded.university.dao.entities.Admin;
import ua.foxminded.university.dao.interfaces.AdminRepository;
import ua.foxminded.university.security.UserRole;
import ua.foxminded.university.validation.Message;
import ua.foxminded.university.validation.UniqueEmailValidator;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class AdminService {
    private final AdminRepository adminRepository;
    private final UniqueEmailValidator emailValidator;

    public int createAdmin(Admin admin) {
        if (emailValidator.isValid(admin)) {
            admin.setRole(UserRole.ADMIN);
            return adminRepository.save(admin).getId();
        }
        log.warn(Message.EMAIL_EXISTS);
        throw new IllegalStateException(Message.EMAIL_EXISTS);
    }

    public int deleteAdminById(int adminId) {
        Optional<Admin> optionalAdmin = adminRepository.findById(adminId);

        if (optionalAdmin.isPresent()) {
            adminRepository.deleteById(adminId);
            log.info(Message.DELETE_SUCCESS);
            return adminId;
        } else {
            log.warn(Message.ADMIN_NOT_FOUND);
            throw new NoSuchElementException(Message.ADMIN_NOT_FOUND);
        }
    }

    public Admin updateAdminById(int adminId, Admin targetAdmin) {
        Admin existingAdmin = adminRepository.findById(adminId).orElseThrow(() -> {
            log.warn(Message.ADMIN_NOT_FOUND, adminId);
            return new NoSuchElementException(Message.ADMIN_NOT_FOUND);
        });

        if (!emailValidator.isValid(targetAdmin)
                && !findAdminByEmail(existingAdmin.getEmail()).get().getEmail().equals(targetAdmin.getEmail())) {
            log.warn(Message.EMAIL_EXISTS);
            throw new IllegalStateException(Message.EMAIL_EXISTS);
        }
        BeanUtils.copyProperties(targetAdmin, existingAdmin, "id", "hashedPassword", "role");
        return adminRepository.save(existingAdmin);
    }

    public Admin changeAdminPasswordById(int adminId, String oldPassword, String newPassword) {
        Admin existingAdmin = adminRepository.findById(adminId).orElseThrow(() -> {
            log.warn(Message.ADMIN_NOT_FOUND);
            return new NoSuchElementException(Message.ADMIN_NOT_FOUND);
        });

        if (!existingAdmin.isPasswordValid(oldPassword)) {
            log.warn(Message.PASSWORD_WRONG);
            throw new IllegalStateException(Message.PASSWORD_WRONG);
        }
        existingAdmin.setPassword(newPassword);
        return adminRepository.save(existingAdmin);
    }

    public List<Admin> getAllAdmins() {
        return adminRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    public Optional<Admin> findAdminById(int adminId) {
        return adminRepository.findById(adminId);
    }

    public Optional<Admin> findAdminByEmail(String email) {
        return adminRepository.findByEmail(email);
    }
}
