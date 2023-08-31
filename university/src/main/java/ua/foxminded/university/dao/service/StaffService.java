package ua.foxminded.university.dao.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ua.foxminded.university.dao.entities.Staff;
import ua.foxminded.university.dao.interfaces.StaffRepository;
import ua.foxminded.university.security.UserRole;
import ua.foxminded.university.validation.Message;
import ua.foxminded.university.validation.UniqueEmailValidator;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class StaffService {
    private final StaffRepository staffRepository;
    private final UniqueEmailValidator emailValidator;

    public int createStaff(Staff staff) {
        if (emailValidator.isValid(staff)) {
            staff.setRole(UserRole.STAFF);
            Staff newStaff = staffRepository.save(staff);
            log.info(Message.CREATE_SUCCESS);
            return newStaff.getId();
        }
        log.warn(Message.EMAIL_EXISTS);
        throw new IllegalStateException(Message.EMAIL_EXISTS);
    }

    public int deleteStaffById(int staffId) {
        Optional<Staff> optionalStaff = staffRepository.findById(staffId);

        if (optionalStaff.isPresent()) {
            staffRepository.deleteById(staffId);
            log.info(Message.DELETE_SUCCESS);
            return staffId;
        } else {
            log.warn(Message.STAFF_NOT_FOUND);
            throw new NoSuchElementException(Message.STAFF_NOT_FOUND);
        }
    }

    public Staff updateStaffById(int staffId, Staff targetStaff) {
        Staff existingStaff = staffRepository.findById(staffId).orElseThrow(() -> {
            log.warn(Message.STAFF_NOT_FOUND);
            return new NoSuchElementException(Message.STAFF_NOT_FOUND);
        });

        if (!emailValidator.isValid(targetStaff)
                && !findStaffByEmail(existingStaff.getEmail()).get().getEmail().equals(targetStaff.getEmail())) {
            log.warn(Message.EMAIL_EXISTS);
            throw new IllegalStateException(Message.EMAIL_EXISTS);
        }
        BeanUtils.copyProperties(targetStaff, existingStaff, "id", "hashedPassword", "role");
        return staffRepository.save(existingStaff);
    }

    public Staff changeStaffPasswordById(int staffId, String oldPassword, String newPassword) {
        Staff existingStaff = staffRepository.findById(staffId).orElseThrow(() -> {
            log.warn(Message.STAFF_NOT_FOUND);
            return new NoSuchElementException(Message.STAFF_NOT_FOUND);
        });

        if (!existingStaff.isPasswordValid(oldPassword)) {
            log.warn(Message.PASSWORD_WRONG);
            throw new IllegalStateException(Message.PASSWORD_WRONG);
        }
        existingStaff.setPassword(newPassword);
        return staffRepository.save(existingStaff);
    }

    public List<Staff> getAllStaff() {
        return staffRepository.findAll();
    }

    public Optional<Staff> findStaffById(int staffId) {
        return staffRepository.findById(staffId);
    }

    public List<Staff> findStaffByName(String firstName, String lastName) {
        return staffRepository.findStaffByFirstNameAndLastName(firstName, lastName);
    }

    public List<Staff> findStaffByPosition(String position) {
        return staffRepository.findStaffByPosition(position);
    }

    public Optional<Staff> findStaffByEmail(String email) {
        return staffRepository.findByEmail(email);
    }
}
