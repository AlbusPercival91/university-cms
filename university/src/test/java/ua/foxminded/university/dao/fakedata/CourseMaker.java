package ua.foxminded.university.dao.fakedata;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.stereotype.Component;
import com.github.javafaker.Faker;

@Component
public class CourseMaker {
	private Faker faker = new Faker();
	private PersonMaker student = new PersonMaker();

	public Set<String> generateCourses(int quantity) {
		return IntStream.range(0, quantity).<String>mapToObj(i -> faker.educator().course())
				.collect(Collectors.toCollection(() -> new HashSet<>(quantity)));
	}

	public Map<Integer, Set<Integer>> assignCourseId() {
		Map<Integer, Set<Integer>> studentCourseID = new HashMap<>();
		int studentsQtty = student.generateFullNames(student.generateNames(20), student.generateLastNames(20)).size();

		IntStream.range(1, studentsQtty + 1).forEachOrdered(i -> {
			Set<Integer> courseID = new HashSet<>();
			Integer localSetSize = ThreadLocalRandom.current().nextInt(1, 4);

			IntStream.range(0, localSetSize).mapToObj(j -> ThreadLocalRandom.current().nextInt(1, 11))
					.filter(rand -> Collections.frequency(courseID, rand) < 3).forEachOrdered(courseID::add);
			studentCourseID.put(i, courseID);
		});
		return studentCourseID;
	}

}
