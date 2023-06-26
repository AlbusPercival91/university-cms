package ua.foxminded.university.dao.entities;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode(exclude = "hashedPassword")
@NoArgsConstructor
@MappedSuperclass
public class Person {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "last_name")
	private String lastName;

	@Column(name = "active")
	private boolean isActive;

	@Column(name = "email")
	private String email;

	@ToString.Exclude
	@Getter(value = AccessLevel.NONE)
	@Setter(value = AccessLevel.NONE)
	@Column(name = "password")
	private String hashedPassword;

	public Person(String firstName, String lastName, boolean isActive, String email, String password) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.isActive = isActive;
		this.email = email;
		setPassword(password);
	}

	public void setPassword(String password) {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		this.hashedPassword = passwordEncoder.encode(password);
	}

	public boolean isPasswordValid(String password) {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		return passwordEncoder.matches(password, hashedPassword);
	}
}
