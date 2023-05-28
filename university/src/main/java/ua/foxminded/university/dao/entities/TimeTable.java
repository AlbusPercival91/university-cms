package ua.foxminded.university.dao.entities;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

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
@Table(name = "timetable", schema = "university")
public class TimeTable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "timetable_id")
	private int id;

	@Column(name = "time_start")
	@DateTimeFormat(pattern = "yyyy-MMM-dd HH:mm")
	private LocalDateTime timeStart;

	@Column(name = "time_end")
	@DateTimeFormat(pattern = "yyyy-MMM-dd HH:mm")
	private LocalDateTime timeEnd;

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
}
