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
import ua.foxminded.university.dao.validation.UniqueEmailValidator;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class StaffService {
	private final StaffRepository staffRepository;
	private final UniqueEmailValidator emailValidator;

	public int createStaff(Staff staff) {
		if (emailValidator.isValid(staff)) {
			Staff newStaff = staffRepository.save(staff);
			log.info("Created staff with id: {}", newStaff.getId());
			return newStaff.getId();
		}
		log.warn("Email already registered");
		throw new IllegalStateException("Email already registered");
	}

	public int deleteStaffById(int staffId) {
		Optional<Staff> optionalStaff = staffRepository.findById(staffId);

		if (optionalStaff.isPresent()) {
			staffRepository.deleteById(staffId);
			log.info("Deleted staff with id: {}", staffId);
			return staffId;
		} else {
			log.warn("Staff with id {} not found", staffId);
			throw new NoSuchElementException("Staff not found");
		}
	}

	public Staff updateStaffById(int staffId, Staff targetStaff) {
		Staff existingStaff = staffRepository.findById(staffId).orElseThrow(() -> {
			log.warn("Staff with id {} not found", staffId);
			return new NoSuchElementException("Staff not found");
		});
		BeanUtils.copyProperties(targetStaff, existingStaff, "id");
		return staffRepository.save(existingStaff);
	}

	public List<Staff> getAllStaff() {
		return staffRepository.findAll();
	}

	public Optional<Staff> findStaffById(int staffId) {
		return staffRepository.findById(staffId);
	}

	public Optional<Staff> findStaffByName(String firstName, String lastName) {
		return staffRepository.findStaffByFirstNameAndLastName(firstName, lastName);
	}

	public Optional<Staff> findStaffByPosition(String position) {
		return staffRepository.findStaffByPosition(position);
	}
}
