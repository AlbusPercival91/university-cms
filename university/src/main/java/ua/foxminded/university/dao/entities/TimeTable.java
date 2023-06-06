package ua.foxminded.university.dao.entities;

import java.time.LocalDate;
import java.time.LocalTime;
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
@Table(name = "timetable", schema = "university")
public class TimeTable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "timetable_id")
	private int id;

	@Column(name = "date")
	private LocalDate date;

	@Column(name = "time_start")
	private LocalTime timeStart;

	@Column(name = "time_end")
	private LocalTime timeEnd;

	@ManyToOne
	@JoinColumn(name = "teacher_id")
	private Teacher teacher;

	@ManyToOne
	@JoinColumn(name = "student_id")
	private Student student;

	@ManyToOne
	@JoinColumn(name = "course_id")
	private Course course;

	@ManyToOne
	@JoinColumn(name = "group_id")
	private Group group;

	@ManyToOne
	@JoinColumn(name = "classroom_id")
	private ClassRoom classRoom;

	public TimeTable(LocalDate date, LocalTime timeStart, LocalTime timeEnd, Teacher teacher, Course course,
			Group group, ClassRoom classRoom) {
		this.date = date;
		this.timeStart = timeStart;
		this.timeEnd = timeEnd;
		this.teacher = teacher;
		this.course = course;
		this.group = group;
		this.classRoom = classRoom;
	}
}
