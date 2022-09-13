package md.moro.tbot.numberdiscovery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class NumberGameFactory {
    private NumberGameFactory() {
    }

    public static String generateNumberStr() {
        List<Integer> digits = new ArrayList<>(IntStream.rangeClosed(0, 9).boxed().toList());
        Collections.shuffle(digits);
        int x = 0;
        for (int i = 0; i < 4; i++) {
            int d = digits.get(i);
            x += d * (Math.pow(10, i));
        }
        return String.format("%04d", x);
    }

    public static Guess generateNumber() {
        try {
            return new Guess(generateNumberStr());
        } catch (BadNumberException e) {
            return null;
        }
    }

}
