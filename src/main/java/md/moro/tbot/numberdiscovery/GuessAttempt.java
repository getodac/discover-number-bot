package md.moro.tbot.numberdiscovery;

import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Date;

public class GuessAttempt {
    private User user;
    private Date date;
    private Guess guess;
    private final GuessResult guessResult;

    public GuessAttempt(User user, Guess guess, Guess answer) {
        this.user = user;
        this.guess = guess;
        this.date = new Date();
        this.guessResult = guess.compareTo(answer);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Guess getGuess() {
        return guess;
    }

    public void setGuess(Guess guess) {
        this.guess = guess;
    }

    public GuessResult getGuessResult() {
        return guessResult;
    }
}
