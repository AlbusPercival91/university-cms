package ua.foxminded.university.dao.fakedata;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.github.javafaker.service.FakeValuesService;
import com.github.javafaker.service.RandomService;
import ua.foxminded.university.dao.entities.Group;

@Component
public class GroupMaker {

	@Autowired
	private FacultyMaker facultyMaker;

	public List<String> generateGroupNames() {
		FakeValuesService fakeValuesService = new FakeValuesService(new Locale("en-GB"), new RandomService());
		return IntStream.range(0, 10).<String>mapToObj(i -> fakeValuesService.regexify("[a-z]{2}-[0-9]{2}"))
				.collect(Collectors.toCollection(() -> new ArrayList<>(10)));
	}

	public List<Group> generateGroups() {
		List<Group> groupList = new ArrayList<>();

		for (int i = 0; i < facultyMaker.generateFaculties().size(); i++) {
			Group group = new Group(generateGroupNames().get(i), facultyMaker.generateFaculties().get(i));
			groupList.add(group);
		}
		return groupList;
	}

}
