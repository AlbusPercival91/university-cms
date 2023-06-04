package ua.foxminded.university.dao.service;

import java.time.LocalDateTime;
import java.util.Optional;
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

	public TimeTable createTeacherTimeTable(LocalDateTime timeStart, LocalDateTime timeEnd, Teacher teacher,
			Group group, ClassRoom classRoom) {
		TimeTable timeTable = new TimeTable(timeStart, timeEnd, teacher, group, classRoom);
		log.debug("Created timeTable for Teacher: {}", teacher.getFirstName() + " " + teacher.getLastName());
		return timeTableRepository.save(timeTable);
	}

	public TimeTable createStudentTimeTable(LocalDateTime timeStart, LocalDateTime timeEnd, Teacher teacher,
			Student student, Course course, ClassRoom classRoom) {
		TimeTable timeTable = new TimeTable(timeStart, timeEnd, teacher, student, course, classRoom);
		log.debug("Created timeTable for Student: {}", student.getFirstName() + " " + student.getLastName());
		return timeTableRepository.save(timeTable);
	}

	public TimeTable createGroupTimeTable(LocalDateTime timeStart, LocalDateTime timeEnd, Group group, Teacher teacher,
			Course course, ClassRoom classRoom) {
		TimeTable timeTable = new TimeTable(timeStart, timeEnd, group, teacher, course, classRoom);
		log.debug("Created timeTable for Group: {}", group.getGroupName());
		return timeTableRepository.save(timeTable);
	}

	public Optional<TimeTable> getTeacherTimeTable(Teacher teacher) {
		return timeTableRepository.findByTeacher(teacher);
	}

	public Optional<TimeTable> getStudentTimeTable(Student student) {
		return timeTableRepository.findByStudent(student);
	}

	public Optional<TimeTable> getGroupTimeTable(Group group) {
		return timeTableRepository.findByGroup(group);
	}
}
