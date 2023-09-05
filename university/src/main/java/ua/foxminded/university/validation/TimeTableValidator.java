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
            throw new TimeTableValidationException(Message.VALIDATION_FAILED);
        }

        if (timeFrom.isAfter(timeTo)) {
            throw new TimeTableValidationException(Message.TIMING_WRONG);
        }

        if (!teacherRepository.findTeachersRelatedToCourse(course.getCourseName()).contains(teacher)) {
            throw new TimeTableValidationException(Message.IS_NOT_TEACHER_COURSE);
        }

        if (timeTableRepository.teacherIsBusy(date, timeFrom, timeTo, teacher)) {
            throw new TimeTableValidationException(Message.TEACHER_BUSY);
        }
    }

    public void validate(int timeTableIdExclude, LocalDate date, LocalTime timeFrom, LocalTime timeTo, Teacher teacher,
            Course course, ClassRoom classRoom) {
        if (timeTableRepository.timeTableValidationFailedOnUpdate(timeTableIdExclude, date, timeFrom, timeTo,
                classRoom)) {
            throw new TimeTableValidationException(Message.VALIDATION_FAILED);
        }

        if (timeFrom.isAfter(timeTo)) {
            throw new TimeTableValidationException(Message.TIMING_WRONG);
        }

        if (!teacherRepository.findTeachersRelatedToCourse(course.getCourseName()).contains(teacher)) {
            throw new TimeTableValidationException(Message.IS_NOT_TEACHER_COURSE);
        }
    }
}
