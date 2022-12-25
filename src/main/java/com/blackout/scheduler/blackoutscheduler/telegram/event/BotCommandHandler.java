package com.blackout.scheduler.blackoutscheduler.telegram.event;

import com.blackout.scheduler.blackoutscheduler.telegram.Bot;
import com.blackout.scheduler.blackoutscheduler.telegram.constants.CommandConstants;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class BotCommandHandler {

    private final InlineKeyboardMarkup inlineKeyboardMarkup;


    public BotCommandHandler(InlineKeyboardMarkup inlineKeyboardMarkup) {
        this.inlineKeyboardMarkup = inlineKeyboardMarkup;
    }

    public void handle(Bot bot, Update event) throws TelegramApiException {
        Message message = event.getMessage();
        MessageEntity commandEntity = message.getEntities().stream()
                .filter(e -> "bot_command".equals(e.getType()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Ошибка обработки команды"));
        String command = message.getText().substring(commandEntity.getOffset(), commandEntity.getLength());
        if (CommandConstants.START.equals(command)) {
            sendStart(bot, event);
        } else {
            SendMessage response = SendMessage.builder()
                    .chatId(message.getChatId().toString())
                    .text("Эта команда не поддерживается")
                    .build();
            bot.execute(response);
        }
    }

    private void sendStart(Bot bot, Update event) throws TelegramApiException {
        Message message = event.getMessage();
        SendMessage response = SendMessage.builder()
                .chatId(message.getChatId().toString())
                .text("Графики отключений беруться с сайта Yasno. Графики доступны только для Киева. Введите свой адресс и нажмите получить график")
                .replyMarkup(inlineKeyboardMarkup)
                .build();
        bot.execute(response);
    }
}
