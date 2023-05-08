package md.moro.tbot.numberdiscovery;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Component
public class NumberGeneratorBot extends TelegramLongPollingBot {
    private final Logger logger = LogManager.getLogger(getClass());
    @Value("${botToken}")
    private String token;
    @Value("${botName}")
    private String botName;
    private GameProcessor gameProcessor;

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        if (StringUtils.hasText(token)) {
            return token;
        } else {
            return getTokenFromFile();
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Long chatId = update.getMessage().getChatId();
            User user = update.getMessage().getFrom();
            if (update.getMessage().hasEntities()) {
                List<MessageEntity> entityList = update.getMessage().getEntities();
                MessageEntity entity = entityList.get(0);
                gameProcessor.processMessage(new ChatContext(this, chatId, user, entity.getText()));
            }
        }
    }

    @Autowired
    public void setGameProcessor(GameProcessor gameProcessor) {
        this.gameProcessor = gameProcessor;
    }

    private String getTokenFromFile() {
        logger.info("No token was provided. Retrieving token from file.");
        String tokenStr = null;
        try {
            String tokenFilePath = System.getProperty("token-file");
            Path tokenPath = Paths.get(tokenFilePath);
            tokenStr = Files.readString(tokenPath);
        } catch (Exception e) {
            // ignore
        }
        return tokenStr;
    }
}
