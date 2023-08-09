package ua.foxminded.university.dao.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ua.foxminded.university.validation.UniqueEmail;

@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity
@Table(name = "staff", schema = "university")
@UniqueEmail
public class Staff extends Person {

	@Column(name = "position")
	private String position;

	@Column(name = "function")
	private String function;

	public Staff(String firstName, String lastName, boolean isActive, String email, String password, String position,
			String function) {
		super(firstName, lastName, isActive, email, password);
		this.position = position;
		this.function = function;
	}
}
