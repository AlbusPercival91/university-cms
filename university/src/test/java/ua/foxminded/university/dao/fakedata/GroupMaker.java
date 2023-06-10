package ua.foxminded.university.dao.fakedata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.stereotype.Component;
import com.github.javafaker.service.FakeValuesService;
import com.github.javafaker.service.RandomService;

@Component
public class GroupMaker {
	PersonMaker student = new PersonMaker();

	public List<String> generateGroups() {
		FakeValuesService fakeValuesService = new FakeValuesService(new Locale("en-GB"), new RandomService());
		return IntStream.range(0, 10).<String>mapToObj(i -> fakeValuesService.regexify("[a-z]{2}-[0-9]{2}"))
				.collect(Collectors.toCollection(() -> new ArrayList<>(10)));
	}

	public List<Integer> assignGroupId() {
		List<Integer> groupID = new ArrayList<>();
		int studentsQtty = student.generateFullNames(student.generateNames(20), student.generateLastNames(20)).size();

		for (int i = 0; i < studentsQtty; i++) {
			Integer rand = ThreadLocalRandom.current().nextInt(0, 11);

			if (rand == 0) {
				rand = null;
			}

			if (Collections.frequency(groupID, rand) < 30 && groupID.size() < studentsQtty) {
				groupID.add(rand);

				while (Collections.frequency(groupID, rand) < 10) {
					groupID.add(rand);
				}
			}
		}
		return groupID;
	}

}
