package ua.foxminded.university.validation;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.foxminded.university.dao.entities.Person;

@Component
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, Person> {

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	public UniqueEmailValidator(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public void initialize(UniqueEmail constraintAnnotation) {
	}

	@Override
	public boolean isValid(Person person, ConstraintValidatorContext context) {
		if (person == null || person.getEmail() == null) {
			return true;
		}

		String email = person.getEmail();

		List<String> entityNames = Arrays.asList("Student", "Teacher", "Staff", "Admin");

		for (String entityName : entityNames) {
			String schemaName = "university"; // Replace with your schema name
			String tableName = entityName.toLowerCase();

			String nativeQuery = String.format("SELECT COUNT(*) FROM %s.%s WHERE email = :email", schemaName,
					tableName);

			BigInteger count = (BigInteger) entityManager.createNativeQuery(nativeQuery).setParameter("email", email)
					.getSingleResult();

			if (count.longValue() > 0) {
				return false;
			}
		}

		return true;
	}
}
