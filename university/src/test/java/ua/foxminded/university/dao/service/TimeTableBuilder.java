package ua.foxminded.university.dao.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.foxminded.university.dao.entities.ClassRoom;
import ua.foxminded.university.dao.entities.Course;
import ua.foxminded.university.dao.entities.Group;
import ua.foxminded.university.dao.entities.Teacher;
import ua.foxminded.university.dao.entities.TimeTable;
import ua.foxminded.university.dao.interfaces.ClassRoomRepository;
import ua.foxminded.university.dao.interfaces.CourseRepository;
import ua.foxminded.university.dao.interfaces.GroupRepository;
import ua.foxminded.university.dao.interfaces.TeacherRepository;

@Component
public class TimeTableBuilder {

	@Autowired
	private TimeTableService timeTableService;

	@Autowired
	private CourseRepository courseRepository;

	@Autowired
	private TeacherRepository teacherRepository;

	@Autowired
	private GroupRepository groupRepository;

	@Autowired
	private ClassRoomRepository classRoomRepository;

	public TimeTable saveTimeTable(LocalDate date, LocalTime timeFrom, LocalTime timeTo, int teacherId, int courseId,
			int groupId, int classRoomId) {
		Optional<Teacher> teacher = teacherRepository.findById(teacherId);
		Optional<Course> course = courseRepository.findById(courseId);
		Optional<Group> group = groupRepository.findById(groupId);
		Optional<ClassRoom> classRoom = classRoomRepository.findById(classRoomId);

		return timeTableService.createTimeTable(date, timeFrom, timeTo, teacher.get(), course.get(), group.get(),
				classRoom.get());
	}
}
