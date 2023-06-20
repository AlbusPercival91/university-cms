package ua.foxminded.university.dao.entities;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
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
@Table(name = "students", schema = "university")
public class Student extends Person {

	@ManyToOne
	@JoinColumn(name = "group_id")
	private Group group;

	@ToString.Exclude
	@ManyToMany(cascade = { CascadeType.PERSIST }, fetch = FetchType.LAZY)
	@JoinTable(schema = "university", name = "students_courses", joinColumns = @JoinColumn(name = "student_id"), inverseJoinColumns = @JoinColumn(name = "course_id"))
	private Set<Course> courses = new HashSet<>();

	public Student(String firstName, String lastName, boolean isActive, String email, String password, Group group) {
		super(firstName, lastName, isActive, email, password);
		this.group = group;
	}
}
