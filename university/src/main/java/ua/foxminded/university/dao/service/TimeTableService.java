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
import ua.foxminded.university.dao.exception.TimeTableValidationException;
import ua.foxminded.university.dao.interfaces.GroupRepository;
import ua.foxminded.university.dao.interfaces.StudentRepository;
import ua.foxminded.university.dao.interfaces.TeacherRepository;
import ua.foxminded.university.dao.interfaces.TimeTableRepository;
import ua.foxminded.university.validation.TimeTableValidator;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class TimeTableService {
	private final TimeTableRepository timeTableRepository;
	private final StudentRepository studentRepository;
	private final TeacherRepository teacherRepository;
	private final GroupRepository groupRepository;
	private final TimeTableValidator timeTableValidator;

	public TimeTable createGroupTimeTable(LocalDate date, LocalTime timeFrom, LocalTime timeTo, Teacher teacher,
			Course course, Group group, ClassRoom classRoom) {
		try {
			timeTableValidator.validate(date, timeFrom, timeTo, teacher, course, classRoom);

			TimeTable timeTable = new TimeTable(date, timeFrom, timeTo, teacher, course, group, classRoom);
			log.info("Timetable [date:{}, time from:{}, time to:{}] is scheduled successfully.", timeTable.getDate(),
					timeTable.getTimeFrom(), timeTable.getTimeTo());
			return timeTableRepository.save(timeTable);
		} catch (TimeTableValidationException ex) {
			throw new TimeTableValidationException(ex.getMessage());
		}
	}

	public TimeTable createTimeTableForStudentsAtCourse(LocalDate date, LocalTime timeFrom, LocalTime timeTo,
			Teacher teacher, Course course, ClassRoom classRoom) {
		try {
			timeTableValidator.validate(date, timeFrom, timeTo, teacher, course, classRoom);

			List<Student> studentsRelatedToCourse = studentRepository
					.findStudentsRelatedToCourse(course.getCourseName());

			if (!studentsRelatedToCourse.isEmpty()) {
				TimeTable timeTable = new TimeTable(date, timeFrom, timeTo, teacher, course, classRoom,
						studentsRelatedToCourse);
				log.info("Timetable [date:{}, time from:{}, time to:{}] is scheduled successfully.",
						timeTable.getDate(), timeTable.getTimeFrom(), timeTable.getTimeTo());
				return timeTableRepository.save(timeTable);
			} else {
				log.warn("Students assigned to course {} not found", course.getCourseName());
				throw new NoSuchElementException("Students at this course not found");
			}
		} catch (TimeTableValidationException ex) {
			throw new TimeTableValidationException(ex.getMessage());
		}
	}

	public List<TimeTable> getStudentTimeTable(int studentId) {
		Student existingStudent = studentRepository.findById(studentId).orElseThrow(() -> {
			log.warn("Student with id {} not found", studentId);
			return new NoSuchElementException("Student not found");
		});

		if (timeTableRepository.studentIsAssignedToAnyCourse(existingStudent.getId())) {
			return timeTableRepository.findByStudentOrderByDateAscTimeFromAsc(existingStudent.getId());
		} else {
			return timeTableRepository.findByGroupOrderByDateAscTimeFromAsc(existingStudent.getGroup());
		}
	}

	public List<TimeTable> getTeacherTimeTable(Teacher teacher) {
		Teacher existingTeacher = teacherRepository.findById(teacher.getId()).orElseThrow(() -> {
			log.warn("Teacher with id {} not found", teacher.getId());
			return new NoSuchElementException("Teacher not found");
		});
		return timeTableRepository.findByTeacherOrderByDateAscTimeFromAsc(existingTeacher);
	}

	public List<TimeTable> getStudentsGroupTimeTable(Student student) {
		Student existingStudent = studentRepository.findById(student.getId()).orElseThrow(() -> {
			log.warn("Student with id {} not found", student.getId());
			return new NoSuchElementException("Student not found");
		});
		return timeTableRepository.findByGroupOrderByDateAscTimeFromAsc(existingStudent.getGroup());
	}

	public List<TimeTable> getGroupTimeTable(Group group) {
		Group existingGroup = groupRepository.findById(group.getId()).orElseThrow(() -> {
			log.warn("Group with id {} not found", group.getId());
			return new NoSuchElementException("Group not found");
		});
		return timeTableRepository.findByGroupOrderByDateAscTimeFromAsc(existingGroup);
	}

	public List<TimeTable> getStudentTimeTableByDate(LocalDate dateFrom, LocalDate dateTo, int studentId) {
		Student existingStudent = studentRepository.findById(studentId).orElseThrow(() -> {
			log.warn("Student with id {} not found", studentId);
			return new NoSuchElementException("Student not found");
		});

		if (timeTableRepository.studentIsAssignedToAnyCourse(existingStudent.getId())) {
			return timeTableRepository.findByDateAndStudentOrderByDateAscTimeFromAsc(dateFrom, dateTo,
					existingStudent.getId());
		} else {
			return timeTableRepository.findByDateAndGroupOrderByDateAscTimeFromAsc(dateFrom, dateTo,
					existingStudent.getGroup());
		}
	}

	public List<TimeTable> getTeacherTimeTableByDate(LocalDate dateFrom, LocalDate dateTo, Teacher teacher) {
		Teacher existingTeacher = teacherRepository.findById(teacher.getId()).orElseThrow(() -> {
			log.warn("Teacher with id {} not found", teacher.getId());
			return new NoSuchElementException("Teacher not found");
		});
		return timeTableRepository.findByDateAndTeacherOrderByDateAscTimeFromAsc(dateFrom, dateTo, existingTeacher);
	}

	public List<TimeTable> getStudentsGroupTimeTableByDate(LocalDate dateFrom, LocalDate dateTo, Student student) {
		Student existingStudent = studentRepository.findById(student.getId()).orElseThrow(() -> {
			log.warn("Student with id {} not found", student.getId());
			return new NoSuchElementException("Student not found");
		});
		return timeTableRepository.findByDateAndGroupOrderByDateAscTimeFromAsc(dateFrom, dateTo,
				existingStudent.getGroup());
	}

	public List<TimeTable> getGroupTimeTableByDate(LocalDate dateFrom, LocalDate dateTo, Group group) {
		Group existingGroup = groupRepository.findById(group.getId()).orElseThrow(() -> {
			log.warn("Group with id {} not found", group.getId());
			return new NoSuchElementException("Group not found");
		});
		return timeTableRepository.findByDateAndGroupOrderByDateAscTimeFromAsc(dateFrom, dateTo, existingGroup);
	}

	public List<TimeTable> getAllTimeTables() {
		return timeTableRepository.findAll();
	}

	public List<TimeTable> getAllTimeTablesByDate(LocalDate dateFrom, LocalDate dateTo) {
		return timeTableRepository.findByDateOrderByDateAscTimeFromAsc(dateFrom, dateTo);
	}

	public TimeTable updateTimeTableById(int timeTableId, TimeTable targetTimeTable) {
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

	public Optional<TimeTable> findTimeTableById(int timeTableId) {
		return timeTableRepository.findById(timeTableId);
	}

	public List<TimeTable> findTimeTableByGroupName(String groupName) {
		return timeTableRepository.findByGroupGroupNameOrderByDateAscTimeFromAsc(groupName);
	}

	public List<TimeTable> findTimeTableByCourseName(String groupName) {
		return timeTableRepository.findByCourseCourseNameOrderByDateAscTimeFromAsc(groupName);
	}

	public List<TimeTable> findTimeTablesByDate(LocalDate date) {
		return timeTableRepository.findByDateOrderByDateAscTimeFromAsc(date);
	}
}