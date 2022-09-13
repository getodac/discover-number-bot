package md.moro.tbot.numberdiscovery;

import java.util.HashSet;
import java.util.Set;

public class Guess {
    private final String guessStr;
    private final char[] guessChars = new char[4];
    private final int[] number = new int[4];

    public Guess(String guessStr) throws BadNumberException {
        this.guessStr = guessStr.strip();
        for (byte i = 0; i < 4 && i < this.guessStr.length(); i++) {
            guessChars[i] = this.guessStr.charAt(i);
            number[i] = (byte) Character.getNumericValue(this.guessStr.charAt(i));
            if (number[i] < 0 || number[i] > 9) {
                throw new BadNumberException(String.format("Invalid character '%c' found in %s", guessStr.charAt(i), guessStr));
            }
        }
        Set<Integer> set = new HashSet<>();
        for (int g : number) {
            set.add(g);
        }
        if (set.size() < 4) {
            throw new BadNumberException(String.format("Invalid number %s. There must be 4 digits unique!", guessStr));
        }
    }

    public String getGuessStr() {
        return guessStr;
    }

    public char[] getGuessChars() {
        return guessChars;
    }

    public int[] getNumber() {
        return number;
    }

    public GuessResult compareTo(Guess other) {
        int p = 0;
        int d = 0;
        for (int i = 0; i < number.length; i++) {
            for (int j = 0; j < other.number.length; j++) {
                if (number[i] == other.number[j]) {
                    d++;
                    if (i == j) {
                        p++;
                    }
                }
            }
        }
        return new GuessResult(d, p);
    }

    public static boolean isValidNumber(String str) {
        try {
            new Guess(str);
            return true;
        } catch (BadNumberException bne) {
            // ignore
        }
        return false;
    }
}
