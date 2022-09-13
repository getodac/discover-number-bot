package md.moro.tbot.numberdiscovery;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.User;

public record ChatContext(TelegramLongPollingBot bot, Long chatId, User user, String text) {
}
