package md.moro.tbot.numberdiscovery;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static md.moro.tbot.numberdiscovery.NumberGameFactory.generateNumberStr;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NumberGameFactoryTest {

    @Test
    void testGenerateNumberStr() {
        List<String> numbers = Arrays.asList(generateNumberStr(), generateNumberStr(), generateNumberStr(), generateNumberStr(), generateNumberStr());
        for (String number: numbers) {
            System.out.printf("Number: %s\n", number);
            assertTrue(Guess.isValidNumber(number));
        }
    }
}