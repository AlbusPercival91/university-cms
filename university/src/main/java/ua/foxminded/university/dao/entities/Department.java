package ua.foxminded.university.dao.entities;

import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
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
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "departments", schema = "university")
public class Department {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "department_id")
	private int id;

	@Column(name = "name")
	private String name;

	@ManyToOne
	@JoinColumn(name = "faculty_id")
	private Faculty faculty;

	@ToString.Exclude
	@OneToMany(mappedBy = "department", cascade = { CascadeType.PERSIST }, fetch = FetchType.EAGER)
	private Set<Teacher> teachers;
}
