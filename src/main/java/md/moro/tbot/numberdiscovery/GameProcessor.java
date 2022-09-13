package md.moro.tbot.numberdiscovery;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class GameProcessor {
    public static final String HELP_OP = "/help";
    public static final String STAT_OP = "/stat";
    public static final String START_OP = "/start";
    public static final String GIVEUP_OP = "/giveup";

    //CacheBuilder<Long, NumberGame> game
    private final Cache<Long, NumberGame> games = CacheBuilder.newBuilder().expireAfterAccess(Duration.ofHours(24)).maximumSize(100).build();

    public void processMessage(ChatContext context) {
        getOperation(context).execute(context);
    }

    private GameOperation getOperation(ChatContext chatContext) {
        if (HELP_OP.equals(chatContext.text())) {
            return new GameOperation.HelpOp();
        } else if (START_OP.equals(chatContext.text())) {
            return new GameOperation.StartOp(games);
        } else if (STAT_OP.equals(chatContext.text())) {
            return new GameOperation.StatOp(games);
        } else if (GIVEUP_OP.equals(chatContext.text())) {
            return new GameOperation.GiveUpOp(games);
        } else {
            if (chatContext.text().strip().length() == 5) {
                NumberGame numberGame = games.getIfPresent(chatContext.chatId());
                if (numberGame != null) {
                    return new GameOperation.GuessOp(numberGame);
                } else {
                    return new GameOperation.SendMsgOp("No game found. Start one with /start");
                }
            }
        }
        return new GameOperation.NoOp();
    }
}
