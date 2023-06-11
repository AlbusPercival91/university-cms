package ua.foxminded.university.dao.fakedata;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.stereotype.Component;
import com.github.javafaker.Faker;
import ua.foxminded.university.dao.entities.Course;

@Component
public class CourseMaker {
	private Faker faker = new Faker();

	public Set<String> generateCourseNames(int quantity) {
		return IntStream.range(0, quantity).<String>mapToObj(i -> faker.educator().course())
				.collect(Collectors.toCollection(() -> new HashSet<>(quantity)));
	}

	public List<Course> generateCourses() {
		List<Course> courseList = new ArrayList<>();

		for (String s : generateCourseNames(10)) {
			Course course = new Course(s);
			courseList.add(course);
		}
		return courseList;
	}
}
