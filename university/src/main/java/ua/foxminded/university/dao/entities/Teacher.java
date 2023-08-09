package ua.foxminded.university.dao.entities;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ua.foxminded.university.validation.UniqueEmail;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@Entity
@Table(name = "teacher", schema = "university")
@UniqueEmail
public class Teacher extends Person {

	@ManyToOne
	@JoinColumn(name = "department_id")
	private Department department;

	@ToString.Exclude
	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, fetch = FetchType.LAZY)
	@JoinTable(schema = "university", name = "teachers_courses", joinColumns = @JoinColumn(name = "teacher_id"), inverseJoinColumns = @JoinColumn(name = "course_id"))
	private Set<Course> assignedCourses = new HashSet<>();

	@ToString.Exclude
	@OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<TimeTable> timeTables;

	public Teacher(String firstName, String lastName, boolean isActive, String email, String password,
			Department department) {
		super(firstName, lastName, isActive, email, password);
		this.department = department;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Teacher))
			return false;
		if (!super.equals(o))
			return false;
		Teacher teacher = (Teacher) o;
		return getId() == teacher.getId();
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), getId());
	}
}
