package ua.foxminded.university.dao.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "students_courses", schema = "university")
public class StudentsCourses {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;

	@Column(name = "student_id")
	private int studentId;

	@Column(name = "course_id")
	private int courseId;

	@ManyToOne
	@JoinColumn(name = "timetable_id")
	private TimeTable timetable;

	public StudentsCourses(int studentId, int courseId) {
		this.studentId = studentId;
		this.courseId = courseId;
	}
}
