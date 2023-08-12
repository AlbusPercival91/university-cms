package ua.foxminded.university.dao.validation;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Component;
import ua.foxminded.university.dao.entities.User;

@Component
public class UniqueEmailValidator {

	@PersistenceContext
	private EntityManager entityManager;

	public UniqueEmailValidator(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public boolean isValid(User person) {
		if (person == null || person.getEmail() == null) {
			return true;
		}
		String email = person.getEmail();
		List<String> entityNames = Arrays.asList("Student", "Teacher", "Staff", "Admin");

		for (String entityName : entityNames) {
			String schemaName = "university";
			String tableName = entityName.toLowerCase();
			String nativeQuery = String.format("SELECT COUNT(*) FROM %s.%s WHERE email = :email", schemaName,
					tableName);
			BigInteger count = (BigInteger) entityManager.createNativeQuery(nativeQuery).setParameter("email", email)
					.getSingleResult();

			if (count.intValue() > 0) {
				return false;
			}
		}
		return true;
	}
}
