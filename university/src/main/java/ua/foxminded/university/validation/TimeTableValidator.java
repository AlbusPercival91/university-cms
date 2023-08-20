package ua.foxminded.university.validation;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import ua.foxminded.university.dao.entities.ClassRoom;
import ua.foxminded.university.dao.entities.Course;
import ua.foxminded.university.dao.entities.Teacher;
import ua.foxminded.university.dao.exception.TimeTableValidationException;
import ua.foxminded.university.dao.interfaces.TeacherRepository;
import ua.foxminded.university.dao.interfaces.TimeTableRepository;

@RequiredArgsConstructor
@Component
public class TimeTableValidator {
	private final TimeTableRepository timeTableRepository;
	private final TeacherRepository teacherRepository;

	public void validate(LocalDate date, LocalTime timeFrom, LocalTime timeTo, Teacher teacher, Course course,
			ClassRoom classRoom) {
		if (timeTableRepository.timeTableValidationFailed(date, timeFrom, timeTo, classRoom)) {
			throw new TimeTableValidationException("Validation failed while creating TimeTable");
		}

		if (timeFrom.isAfter(timeTo)) {
			throw new TimeTableValidationException("Timing is wrong");
		}

		if (!teacherRepository.findTeachersRelatedToCourse(course.getCourseName()).contains(teacher)) {
			throw new TimeTableValidationException("Teacher is not assigned with such Course");
		}

		if (timeTableRepository.teacherIsBusy(date, timeFrom, timeTo, teacher)) {
			throw new TimeTableValidationException("Teacher is busy during this time");
		}
	}
}
