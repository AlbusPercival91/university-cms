package ua.foxminded.university.dao.fakedata;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import com.github.javafaker.Faker;

public class ClassRoomMaker {
	Faker faker = new Faker();

	public Set<String> generateStreet(int quantity) {
		return IntStream.range(0, quantity).<String>mapToObj(i -> faker.address().streetName())
				.collect(Collectors.toCollection(() -> new HashSet<>(quantity)));
	}

	public List<Integer> generateBuildningNumber(int quantity) {
		return IntStream.range(0, quantity).<Integer>mapToObj(i -> Integer.parseInt(faker.address().buildingNumber()))
				.collect(Collectors.toCollection(() -> new ArrayList<>(quantity)));
	}

	public List<Integer> generateClassRoomNumber(int quantity) {
		return IntStream.range(0, quantity).<Integer>mapToObj(i -> Integer.parseInt(faker.number().digits(100)))
				.collect(Collectors.toCollection(() -> new ArrayList<>(quantity)));
	}
}
