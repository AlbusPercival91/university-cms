package ua.foxminded.university.dao.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ua.foxminded.university.dao.entities.Admin;
import ua.foxminded.university.dao.interfaces.AdminRepository;
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
			return adminRepository.save(admin).getId();
		}
		log.warn("Email already registered");
		throw new IllegalStateException("Email already registered");
	}

	public int deleteAdminById(int adminId) {
		Optional<Admin> optionalAdmin = adminRepository.findById(adminId);

		if (optionalAdmin.isPresent()) {
			adminRepository.deleteById(adminId);
			log.info("Deleted admin with id: {}", adminId);
			return adminId;
		} else {
			log.warn("Admin with id {} not found", adminId);
			throw new NoSuchElementException("Admin not found");
		}
	}

	public Admin updateAdminById(int adminId, Admin targetAdmin) {
		Admin existingAdmin = adminRepository.findById(adminId).orElseThrow(() -> {
			log.warn("Admin with id {} not found", adminId);
			return new NoSuchElementException("Admin not found");
		});
		BeanUtils.copyProperties(targetAdmin, existingAdmin, "id");
		return adminRepository.save(existingAdmin);
	}

	public List<Admin> getAllAdmins() {
		return adminRepository.findAll();
	}

	public Optional<Admin> findAdminById(int adminId) {
		return adminRepository.findById(adminId);
	}
}