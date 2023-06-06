package ua.foxminded.university.dao.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ua.foxminded.university.dao.entities.ClassRoom;
import ua.foxminded.university.dao.entities.Course;
import ua.foxminded.university.dao.entities.Group;
import ua.foxminded.university.dao.entities.Student;
import ua.foxminded.university.dao.entities.Teacher;
import ua.foxminded.university.dao.entities.TimeTable;
import ua.foxminded.university.dao.interfaces.TimeTableRepository;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class TimeTableService {
	private final TimeTableRepository timeTableRepository;

	public TimeTable createTimeTable(LocalDate date, LocalTime timeStart, LocalTime timeEnd, Teacher teacher,
			Course course, Group group, ClassRoom classRoom) {
		TimeTable timeTable = new TimeTable(date, timeStart, timeEnd, teacher, course, group, classRoom);
		log.info("Timetable [date::{}, time start::{}, time end::{}] is scheduled successfully.", timeTable.getDate(),
				timeTable.getTimeStart(), timeTable.getTimeEnd());
		return timeTableRepository.save(timeTable);
	}

	public List<TimeTable> getTeacherTimeTable(Teacher teacher) {
		return timeTableRepository.findByTeacher(teacher);
	}

	public List<TimeTable> getStudentTimeTable(Student student) {
		return timeTableRepository.findByStudent(student);
	}

	public List<TimeTable> getGroupTimeTable(Group group) {
		return timeTableRepository.findByGroup(group);
	}

	public List<TimeTable> getTeacherTimeTableByDate(LocalDate dateStart, LocalDate dateEnd, Teacher teacher) {
		return timeTableRepository.findByDateAndTeacher(dateStart, dateEnd, teacher);
	}

	public List<TimeTable> getStudentTimeTableByDate(LocalDate dateStart, LocalDate dateEnd, Student student) {
		return timeTableRepository.findByDateAndGroup(dateStart, dateEnd, student.getGroup());
	}

	public List<TimeTable> getGroupTimeTableByDate(LocalDate dateStart, LocalDate dateEnd, Group group) {
		return timeTableRepository.findByDateAndGroup(dateStart, dateEnd, group);
	}

	public List<TimeTable> getTimeTableByDate(LocalDate dateStart, LocalDate dateEnd) {
		return timeTableRepository.findByDate(dateStart, dateEnd);
	}
}
