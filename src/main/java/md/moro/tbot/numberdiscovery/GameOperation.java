package md.moro.tbot.numberdiscovery;

import com.google.common.cache.Cache;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class GameOperation {
    protected Logger logger = LogManager.getLogger(getClass());

    abstract void execute(ChatContext context);

    protected void sendMessage(ChatContext chatContext, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatContext.chatId());
        sendMessage.setText(message);
        sendMessage(chatContext.bot(), sendMessage);
    }

    protected void sendMessage(TelegramLongPollingBot bot, SendMessage message) {
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            logger.error("Couldn't send message '{}' to chat {}", message.getText(), message.getChatId(), e);
        }
    }

    static class NoOp extends GameOperation {
        @Override
        public void execute(ChatContext context) {
            String msg = context.text();
            logger.debug("No Op for: {}", msg);
        }
    }

    static class SendMsgOp extends GameOperation {
        private final String msg;

        SendMsgOp(String msg) {
            this.msg = msg;
        }

        @Override
        public void execute(ChatContext context) {
            sendMessage(context, msg);
        }
    }

    static class HelpOp extends GameOperation {
        static final String HELP = """
                /help - display help
                /start - start new game
                /giveup - give up and show the number
                /stat - display statistics
                /dddd - try to guess the 4 digit number
                """;

        @Override
        public void execute(ChatContext context) {
            logger.debug("Display Help");
            sendMessage(context, HELP);
        }
    }

    static class StartOp extends GameOperation {
        private final Cache<Long, NumberGame> games;

        public StartOp(Cache<Long, NumberGame> games) {
            this.games = games;
        }

        @Override
        public void execute(ChatContext context) {
            logger.debug("Start new Game");
            NumberGame numberGame = games.getIfPresent(context.chatId());
            if (numberGame != null) {
                if (numberGame.isGuessed() || numberGame.isGiveUp()) {
                    games.put(context.chatId(), new NumberGame(context.chatId()));
                    sendMessage(context, "The number was generated. Try to guess it!");
                } else {
                    sendMessage(context, "A game is already in progress. Try to guess the number or /giveup");
                }
            } else {
                games.put(context.chatId(), new NumberGame(context.chatId()));
                sendMessage(context, "The number was generated. Try to guess it!");
            }
        }
    }

    static class StatOp extends GameOperation {
        private final Cache<Long, NumberGame> games;

        public StatOp(Cache<Long, NumberGame> games) {
            this.games = games;
        }

        @Override
        public void execute(ChatContext context) {
            logger.debug("Stats");
            NumberGame numberGame = games.getIfPresent(context.chatId());
            if (numberGame != null) {
                StringBuilder message = new StringBuilder();
                if (numberGame.isGuessed()) {
                    Optional<GuessAttempt> ga = numberGame.getGuessAttempts().stream().filter(g -> g.getGuessResult().isGuessed()).findFirst();
                    ga.ifPresent(guessAttempt -> message.append(String.format("The number was guessed by %s %s", guessAttempt.getUser().getFirstName(), guessAttempt.getUser().getLastName())));
                } else if (numberGame.isGiveUp()) {
                    message.append(String.format("Someone given up. The number to guess was %s", numberGame.getGuess().getGuessStr()));
                }
                message.append("\n\n").append("Total attempts: ").append(numberGame.getGuessAttempts().size());
                Map<User, List<GuessAttempt>> map = numberGame.getGuessAttempts().stream().collect(Collectors.groupingBy(GuessAttempt::getUser));
                for (Map.Entry<User, List<GuessAttempt>> entry: map.entrySet()) {
                    entry.getKey().getFirstName();
                    String firstname = entry.getKey().getFirstName();
                    String lastName = entry.getKey().getLastName() != null ? entry.getKey().getLastName() : "";
                    message.append("\n").append(String.format("%s %s: %d", firstname, lastName, entry.getValue().size()));
                }
                sendMessage(context, message.toString());
            } else {
                sendMessage(context, "No game found. Start one with /start");
            }
        }
    }

    static class GuessOp extends GameOperation {
        private final NumberGame numberGame;

        public GuessOp(NumberGame numberGame) {
            this.numberGame = numberGame;
        }

        @Override
        public void execute(ChatContext context) {
            String number = context.text().strip().substring(1);
            try {
                Guess userGuess = new Guess(number);
                GuessAttempt ga = numberGame.tryToGuess(context.user(), userGuess);
                if (ga.getGuessResult().isGuessed()) {
                    sendMessage(context, String.format("Yaaa, you did it. The number is: '%s'.", ga.getGuess().getGuessStr()));
                } else {
                    int d = ga.getGuessResult().digits();
                    int p = ga.getGuessResult().positions();
                    String message = d == 0 ? "You didn't guess any digit" : "You guessed %d digit%s, %d digit%s on the correct position.";
                    sendMessage(context, String.format(message, d, d > 1 ? "s" : "", p, p > 1 ? "s are" : " is"));
                }
            } catch (TriesExceedLimitException | BadNumberException e) {
                sendMessage(context, e.getMessage());
            } catch (NumberAlreadyGuessedException e) {
                sendMessage(context, "Ended Game. See /stat or start new one with /start");
            }
        }
    }

    static class GiveUpOp extends GameOperation {
        private final Cache<Long, NumberGame> games;

        public GiveUpOp(Cache<Long, NumberGame> games) {
            this.games = games;
        }

        @Override
        public void execute(ChatContext context) {
            NumberGame numberGame = games.getIfPresent(context.chatId());
            if (numberGame != null) {
                logger.debug("Slabak! The number to guess was <code>{}</code>", numberGame.getGuess().getGuessStr());
                numberGame.giveUp();
                sendMessage(context, String.format("Slabak! The number to guess was %s", numberGame.getGuess().getGuessStr()));
            } else {
                sendMessage(context, "No game found. Start one with /start");
            }
        }
    }

}
