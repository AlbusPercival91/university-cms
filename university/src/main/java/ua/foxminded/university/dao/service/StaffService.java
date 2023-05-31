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

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class StaffService {
	private final StaffRepository staffRepository;

	public int createStaff(Staff staff) {
		Staff newStaff = staffRepository.save(staff);
		log.info("Created staff with id: {}", newStaff.getId());
		return newStaff.getId();
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

	public Staff updateStaffById(int staffId, Staff staff) {
		Staff existingStaff = staffRepository.findById(staffId).orElseThrow(() -> {
			log.warn("Staff with id {} not found", staffId);
			return new NoSuchElementException("Staff not found");
		});
		BeanUtils.copyProperties(staff, existingStaff, "id");
		return staffRepository.save(existingStaff);
	}

	public List<Staff> getAllStaff() {
		return staffRepository.findAll();
	}
}
