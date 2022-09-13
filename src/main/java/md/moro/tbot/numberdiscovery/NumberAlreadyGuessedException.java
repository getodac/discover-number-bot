package md.moro.tbot.numberdiscovery;

public class NumberAlreadyGuessedException extends Exception {
    public NumberAlreadyGuessedException(String message) {
        super(message);
    }
}
