package ua.foxminded.university.validation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class IdCollectorTest {

    private IdCollector idCollector;

    @BeforeEach
    void init() {
        idCollector = new IdCollector();
    }

    @ParameterizedTest
    @CsvSource({ "1", "2", "3" })
    void testCollect_WhenOnlyDigitsInput(String digitString) {
        int digit = Integer.parseInt(digitString);
        Assertions.assertEquals(digit, idCollector.collect(digitString));
    }

    @ParameterizedTest
    @CsvSource({ "as1f, 1", "2qwds, 2", "a#3, 3" })
    void testCollect_WhenDigitsAndLettersInput(String digitsLetters, String digitStringExpected) {
        int digits = Integer.parseInt(digitStringExpected);
        Assertions.assertEquals(digits, idCollector.collect(digitsLetters));
    }

    @ParameterizedTest
    @CsvSource({ "asf, 0", "qwds, 0", "a#%*)(, 0" })
    void testCollect_WhenOnlyLettersInput(String digitStringInit, String digitStringExpected) {
        int digit = Integer.parseInt(digitStringExpected);
        Assertions.assertEquals(digit, idCollector.collect(digitStringInit));
    }
}
