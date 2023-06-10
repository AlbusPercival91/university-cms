package ua.foxminded.university.dao.fakedata;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import com.github.javafaker.Faker;

public class StaffMaker {
	Faker faker = new Faker();

	public Set<String> generatePositions(int quantity) {
		return IntStream.range(0, quantity).<String>mapToObj(i -> faker.job().position())
				.collect(Collectors.toCollection(() -> new HashSet<>(quantity)));
	}
}
