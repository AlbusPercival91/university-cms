package ua.foxminded.university.dao.fakedata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.stereotype.Component;
import com.github.javafaker.Faker;

@Component
public class PersonMaker {
	Faker faker = new Faker();

	public List<String> generateNames(int quantity) {
		return IntStream.range(0, quantity).<String>mapToObj(i -> faker.name().firstName())
				.collect(Collectors.toCollection(() -> new ArrayList<>(quantity)));
	}

	public List<String> generateLastNames(int quantity) {
		return IntStream.range(0, quantity).<String>mapToObj(i -> faker.name().lastName())
				.collect(Collectors.toCollection(() -> new ArrayList<>(quantity)));
	}

	public Set<String> generateFullNames(List<String> names, List<String> lastNames) {
		Set<String> list = new HashSet<>();
		while (list.size() != 200) {
			Arrays.asList(names, lastNames).forEach(Collections::shuffle);
			Collections.addAll(list, names.get(0) + " " + lastNames.get(0));
		}
		return list;
	}

	public Set<String> generateEmails(int quantity) {
		return IntStream.range(0, quantity).<String>mapToObj(i -> faker.internet().emailAddress())
				.collect(Collectors.toCollection(() -> new HashSet<>(quantity)));
	}

	public List<String> generatePasswords(int quantity) {
		return IntStream.range(0, quantity).<String>mapToObj(i -> faker.internet().password(8, 60, true, true))
				.collect(Collectors.toCollection(() -> new ArrayList<>(quantity)));
	}

}
