package ua.foxminded.university.entities;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
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
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "courses", schema = "university")
public class Course {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "course_id")
	private int id;

	@Column(name = "course_name")
	private String courseName;

	@Column(name = "course_description")
	private String courseDescription;

	@ToString.Exclude
	@ManyToMany(mappedBy = "teacherCourses", cascade = { CascadeType.PERSIST }, fetch = FetchType.EAGER)
	private Set<Teacher> teachers = new HashSet<>();

	@ToString.Exclude
	@ManyToMany(mappedBy = "studentCourses", cascade = { CascadeType.PERSIST }, fetch = FetchType.EAGER)
	private Set<Student> students = new HashSet<>();
}
