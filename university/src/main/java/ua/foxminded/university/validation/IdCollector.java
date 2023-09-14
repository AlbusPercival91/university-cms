package ua.foxminded.university.validation;

import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class IdCollector {

    public int collect(String input) {
        String idCollect = input.chars().filter(Character::isDigit).mapToObj(Character::toString)
                .collect(Collectors.joining());
        return idCollect.isEmpty() ? 0 : Integer.parseInt(idCollect);
    }

}
