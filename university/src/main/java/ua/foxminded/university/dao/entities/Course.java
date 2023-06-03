package ua.foxminded.university.dao.entities;

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
import javax.persistence.OneToMany;
import javax.persistence.Table;
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
@Entity
@Table(name = "courses", schema = "university")
public class Course {

	@ToString.Exclude
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "course_id")
	private int id;

	@Column(name = "course_name")
	private String courseName;

	@Column(name = "course_description")
	private String courseDescription;

	@ToString.Exclude
	@ManyToMany(mappedBy = "teachersCourses", cascade = { CascadeType.PERSIST }, fetch = FetchType.EAGER)
	private Set<Teacher> teachers = new HashSet<>();

	@ToString.Exclude
	@ManyToMany(mappedBy = "studentsCourses", cascade = { CascadeType.PERSIST }, fetch = FetchType.EAGER)
	private Set<Student> students = new HashSet<>();

	@ToString.Exclude
	@OneToMany(mappedBy = "course")
	private Set<TimeTable> timeTables;

	public Course(String courseName, String courseDescription) {
		this.courseName = courseName;
		this.courseDescription = courseDescription;
	}
	
	
}
