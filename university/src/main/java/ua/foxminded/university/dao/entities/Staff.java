package ua.foxminded.university.dao.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity
@Table(name = "staff", schema = "university")
public class Staff extends Person {

	@Column(name = "position")
	private String position;

	public Staff(String firstName, String lastName, boolean isActive, String email, String password, String position) {
		super(firstName, lastName, isActive, email, password);
		this.position = position;
	}
}
