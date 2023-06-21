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
import javax.persistence.OneToMany;
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
@Table(name = "teachers", schema = "university")
public class Teacher extends Person {

	@ManyToOne
	@JoinColumn(name = "department_id")
	private Department department;

	@ManyToOne
	@JoinColumn(name = "course_id")
	private Course course;

	@ToString.Exclude
	@ManyToMany(cascade = { CascadeType.PERSIST }, fetch = FetchType.LAZY)
	@JoinTable(schema = "university", name = "teachers_courses", joinColumns = @JoinColumn(name = "teacher_id"), inverseJoinColumns = @JoinColumn(name = "course_id"))
	private Set<Course> courses = new HashSet<>();

	@ToString.Exclude
	@OneToMany(mappedBy = "teacher")
	private Set<TimeTable> timeTables;

	public Teacher(String firstName, String lastName, boolean isActive, String email, String password,
			Department department, Course course) {
		super(firstName, lastName, isActive, email, password);
		this.department = department;
		this.course = course;
	}
}
