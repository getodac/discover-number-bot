package md.moro.tbot.numberdiscovery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.List;

@Component
public class NumberGeneratorBot extends TelegramLongPollingBot {
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
        return token;
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
}
