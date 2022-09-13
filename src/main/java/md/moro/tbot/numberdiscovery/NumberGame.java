package md.moro.tbot.numberdiscovery;

import org.telegram.telegrambots.meta.api.objects.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NumberGame {
    private final Long chatId;
    private final Guess guess;
    private final List<GuessAttempt> guessAttempts = new ArrayList<>(100);
    private boolean guessed;
    private boolean giveUp;

    public NumberGame(Long chatId) {
        this.chatId = chatId;
        this.guess = NumberGameFactory.generateNumber();
    }

    public Long getChatId() {
        return chatId;
    }

    public Guess getGuess() {
        return guess;
    }

    public List<GuessAttempt> getGuessAttempts() {
        return Collections.unmodifiableList(guessAttempts);
    }


    public GuessAttempt tryToGuess(User user, Guess guess) throws TriesExceedLimitException, NumberAlreadyGuessedException {
        if (guessed || giveUp) {
            throw new NumberAlreadyGuessedException(String.format("Number already guessed. The number was '%s'. Your number is '%s'", this.guess.getGuessStr(), guess.getGuessStr()));
        } else {
            if (guessAttempts.size() < 100) {
                GuessAttempt ga = new GuessAttempt(user, guess, this.guess);
                guessAttempts.add(ga);
                guessed = ga.getGuessResult().isGuessed();
                return ga;
            } else {
                throw new TriesExceedLimitException("Exceeded limit to try");
            }
        }
    }

    public boolean isGuessed() {
        return guessed;
    }

    public boolean isGiveUp() {
        return giveUp;
    }

    public void giveUp() {
        this.giveUp = true;
    }
}
