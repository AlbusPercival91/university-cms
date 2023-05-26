package ua.foxminded.university.entities;

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
@Table(name = "faculties", schema = "university")
public class Faculty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "faculty_id")
    private int id;

    @Column(name = "faculty_name")
    private String facultyName;

    @OneToMany(mappedBy = "faculty", cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    private Set<Group> groupsInFacultySet;

    @ToString.Exclude
    @OneToMany(mappedBy = "faculty", cascade = { CascadeType.PERSIST }, fetch = FetchType.EAGER)
    private Set<Department> departments;
}
