package ua.foxminded.university.dao.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ua.foxminded.university.dao.entities.ClassRoom;
import ua.foxminded.university.dao.entities.Course;
import ua.foxminded.university.dao.entities.Group;
import ua.foxminded.university.dao.entities.Student;
import ua.foxminded.university.dao.entities.Teacher;
import ua.foxminded.university.dao.entities.TimeTable;
import ua.foxminded.university.dao.interfaces.GroupRepository;
import ua.foxminded.university.dao.interfaces.StudentRepository;
import ua.foxminded.university.dao.interfaces.TeacherRepository;
import ua.foxminded.university.dao.interfaces.TimeTableRepository;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class TimeTableService {
	private final TimeTableRepository timeTableRepository;
	private final StudentRepository studentRepository;
	private final TeacherRepository teacherRepository;
	private final GroupRepository groupRepository;

	public TimeTable createTimeTable(LocalDate date, LocalTime timeFrom, LocalTime timeEnd, Teacher teacher,
			Course course, Group group, ClassRoom classRoom) {
		TimeTable timeTable = new TimeTable(date, timeFrom, timeEnd, teacher, course, group, classRoom);
		log.info("Timetable [date::{}, time start::{}, time end::{}] is scheduled successfully.", timeTable.getDate(),
				timeTable.getTimeStart(), timeTable.getTimeEnd());
		return timeTableRepository.save(timeTable);
	}

	public List<TimeTable> getTeacherTimeTable(Teacher teacher) {
		Teacher existingTeacher = teacherRepository.findById(teacher.getId()).orElseThrow(() -> {
			log.warn("Teacher with id {} not found", teacher.getId());
			return new NoSuchElementException("Teacher not found");
		});
		return timeTableRepository.findByTeacher(existingTeacher);
	}

	public List<TimeTable> getStudentTimeTable(Student student) {
		Student existingStudent = studentRepository.findById(student.getId()).orElseThrow(() -> {
			log.warn("Student with id {} not found", student.getId());
			return new NoSuchElementException("Student not found");
		});
		return timeTableRepository.findByStudent(existingStudent);
	}

	public List<TimeTable> getGroupTimeTable(Group group) {
		Group existingGroup = groupRepository.findById(group.getId()).orElseThrow(() -> {
			log.warn("Group with id {} not found", group.getId());
			return new NoSuchElementException("Group not found");
		});
		return timeTableRepository.findByGroup(existingGroup);
	}

	public List<TimeTable> getTeacherTimeTableByDate(LocalDate dateFrom, LocalDate dateTo, Teacher teacher) {
		Teacher existingTeacher = teacherRepository.findById(teacher.getId()).orElseThrow(() -> {
			log.warn("Teacher with id {} not found", teacher.getId());
			return new NoSuchElementException("Teacher not found");
		});
		return timeTableRepository.findByDateAndTeacher(dateFrom, dateTo, existingTeacher);
	}

	public List<TimeTable> getStudentTimeTableByDate(LocalDate dateFrom, LocalDate dateTo, Student student) {
		Student existingStudent = studentRepository.findById(student.getId()).orElseThrow(() -> {
			log.warn("Student with id {} not found", student.getId());
			return new NoSuchElementException("Student not found");
		});
		return timeTableRepository.findByDateAndGroup(dateFrom, dateTo, existingStudent.getGroup());
	}

	public List<TimeTable> getGroupTimeTableByDate(LocalDate dateFrom, LocalDate dateTo, Group group) {
		Group existingGroup = groupRepository.findById(group.getId()).orElseThrow(() -> {
			log.warn("Group with id {} not found", group.getId());
			return new NoSuchElementException("Group not found");
		});
		return timeTableRepository.findByDateAndGroup(dateFrom, dateTo, existingGroup);
	}

	public List<TimeTable> getAllTimeTablesByDate(LocalDate dateFrom, LocalDate dateTo) {
		return timeTableRepository.findByDate(dateFrom, dateTo);
	}

	public TimeTable updateTimeTabletById(int timeTableId, TimeTable targetTimeTable) {
		TimeTable existingTimeTable = timeTableRepository.findById(timeTableId).orElseThrow(() -> {
			log.warn("TimeTable with id {} not found", timeTableId);
			return new NoSuchElementException("TimeTable not found");
		});
		BeanUtils.copyProperties(targetTimeTable, existingTimeTable, "id");
		return timeTableRepository.save(existingTimeTable);
	}

	public int deleteTimeTableById(int timeTableId) {
		Optional<TimeTable> optionalTimeTable = timeTableRepository.findById(timeTableId);

		if (optionalTimeTable.isPresent()) {
			timeTableRepository.deleteById(timeTableId);
			log.info("Deleted TimeTable with id: {}", timeTableId);
			return timeTableId;
		} else {
			log.warn("TimeTable with id {} not found", timeTableId);
			throw new NoSuchElementException("TimeTable not found");
		}
	}
}
