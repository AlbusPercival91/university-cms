package ua.foxminded.university.dao.entities;

import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity
@Table(name = "admin", schema = "university")
public class Admin extends User {

	public Admin(String firstName, String lastName, boolean isActive, String email, String password) {
		super(firstName, lastName, isActive, email, password);
	}
}
