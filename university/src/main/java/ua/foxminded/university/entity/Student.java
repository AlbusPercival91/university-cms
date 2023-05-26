package ua.foxminded.university.entity;

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
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
@Entity
@Table(name = "students", schema = "university")
public class Student extends Person {

	@ManyToOne
	@JoinColumn(name = "group_id")
	private Group group;

	@ToString.Exclude
	@OneToMany(mappedBy = "group", cascade = { CascadeType.PERSIST }, fetch = FetchType.EAGER)
	private Set<Group> groups;

	@ToString.Exclude
	@ManyToMany(cascade = { CascadeType.PERSIST }, fetch = FetchType.EAGER)
	@JoinTable(name = "students_courses", joinColumns = @JoinColumn(name = "student_id"), inverseJoinColumns = @JoinColumn(name = "course_id"))
	private Set<Course> studentCourses = new HashSet<>();
}
