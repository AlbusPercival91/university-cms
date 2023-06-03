package ua.foxminded.university.dao.entities;

import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
@Table(name = "faculties", schema = "university")
public class Faculty {

	@ToString.Exclude
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "faculty_id")
	private int id;

	@Column(name = "faculty_name")
	private String facultyName;

	@ToString.Exclude
	@OneToMany(mappedBy = "faculty", cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
	private Set<Group> groups;

	@ToString.Exclude
	@OneToMany(mappedBy = "faculty", cascade = { CascadeType.PERSIST }, fetch = FetchType.EAGER)
	private Set<Department> departments;

	public Faculty(String facultyName) {
		this.facultyName = facultyName;
	}
}
