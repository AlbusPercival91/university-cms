package ua.foxminded.university.dao.service;

import java.time.LocalDateTime;
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

	public TimeTable createTimeTable(LocalDateTime timeStart, LocalDateTime timeEnd, Teacher teacher, Student student,
			Course course, Group group, ClassRoom classRoom) {
		TimeTable timeTable = new TimeTable(timeStart, timeEnd, teacher, student, course, group, classRoom);
		log.info("Timetable [time start::{}, time end::{}] is scheduled successfully.", timeTable.getTimeStart(),
				timeTable.getTimeEnd());
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
}
